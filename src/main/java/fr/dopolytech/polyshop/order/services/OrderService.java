package fr.dopolytech.polyshop.order.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.order.dtos.GetOrderDto;
import fr.dopolytech.polyshop.order.messages.ErrorMessage;
import fr.dopolytech.polyshop.order.messages.InventoryMessage;
import fr.dopolytech.polyshop.order.messages.MessageConfirmed;
import fr.dopolytech.polyshop.order.messages.ProductItem;
import fr.dopolytech.polyshop.order.messages.ShoppingCartMessage;
import fr.dopolytech.polyshop.order.models.Order;
import fr.dopolytech.polyshop.order.models.OrderStatus;
import fr.dopolytech.polyshop.order.models.Product;
import fr.dopolytech.polyshop.order.repositories.OrderRepository;
import fr.dopolytech.polyshop.order.repositories.ProductRepository;

@Component
public class OrderService {

    private final RabbitTemplate rabbitTemplate;
    private final Queue orderCancelQueue;
    private final Queue inventoryQueue;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    // Define the logger
  private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

  @Autowired
  public OrderService(RabbitTemplate rabbitTemplate, Queue orderCancelQueue, Queue inventoryQueue,
      OrderRepository orderRepository,
      ProductRepository productRepository
      ) {
    this.rabbitTemplate = rabbitTemplate;
    this.orderCancelQueue = orderCancelQueue;
    this.inventoryQueue = inventoryQueue;
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
  }

  public Order createOrder(Order order) {
    return orderRepository.save(order);
  }

  public GetOrderDto findById(long id) {
    Order order = orderRepository.findById(id).orElse(null);
    if (order == null) {
      return null;
    }

    List<Product> products = productRepository.findByOrderId(order.id);
    List<ProductItem> productItems = products.stream().map(product -> {
      return new ProductItem(product.getProductId(), product.getQuantity() );
    }).collect(Collectors.toList());

    return new GetOrderDto(order.id, order.date, order.status, productItems);
  }

  public Iterable<GetOrderDto> findAll() {
    List<Order> orders = orderRepository.findAll();

    //For each orders, map the order to a GetOrderDto by retrieving the products in the product repository
    return orders.stream().map(order -> {
      List<Product> products = productRepository.findByOrderId(order.id);
      // Transfrom the List of Product to a List of ProductItem
      List<ProductItem> productItems = products.stream().map(product -> {
        return new ProductItem(product.getProductId(), product.getQuantity() );
      }).collect(Collectors.toList());
      return new GetOrderDto(order.id, order.date, order.status, productItems);
    }).collect(Collectors.toList());
  }

  // This method is called when a message is received from the Shopping Card service
  public void receiveMessage(String message) {
    logger.info("Received message from shopping cart : {}", message);

    // Receive a message from the Shopping Card service and handle it

    try {
      ObjectMapper mapper = new ObjectMapper();
      ShoppingCartMessage shoppingCartMessage = mapper.readValue(message, ShoppingCartMessage.class);

      // Save the order and the products in the database
      Order order = orderRepository.save(new Order());
      logger.info("Create a new order : ", order.toString());

      shoppingCartMessage.products.forEach(product -> {
        Product productToSave = new Product( product.productId, product.amount, order.id);
        productRepository.save(productToSave);
      });
      
      // Transfer the message to the Inventory service
      InventoryMessage inventoryMessage = new InventoryMessage(order.id, shoppingCartMessage.products);
      logger.info("Send message to inventory service : ", inventoryMessage);
      String inventoryMessageString = mapper.writeValueAsString(inventoryMessage);
      rabbitTemplate.convertAndSend(inventoryQueue.getName(), inventoryMessageString.toString());

      
    } catch (Exception e) {
      logger.error("Error while parsing the message: {}", e.getMessage());
    }
  }


  // This method is called when a Cancel message is received from the Inventory service
  public void receiveMessageCancel(String message) {
    logger.info("Received cancel message: {}", message);

    // Receive a message from the Inventory service and handle them

    try {
      ObjectMapper mapper = new ObjectMapper();
      ErrorMessage errorMessage = mapper.readValue(message, ErrorMessage.class);

      // Cancel the order in the database,and update the status, depending on the error

      Order order = orderRepository.findById(errorMessage.orderId).get();

      // Make a switch on the error message to update the status of the order
      switch (errorMessage.source) {
        case "missing_stock":
          order.status = OrderStatus.missing_stock;
          break;
        case "missing_payment":
          order.status = OrderStatus.missing_payment;
          break;
        case "missing_shipping":
          order.status = OrderStatus.missing_shipping;
          break;
        default:
          logger.error("Error while parsing the error message: {}", errorMessage.source);
          break;
      }


      orderRepository.save(order);

      // Transfer the message to the Shopping Card service
      String errorMessageString = mapper.writeValueAsString(errorMessage);
      rabbitTemplate.convertAndSend(orderCancelQueue.getName(), errorMessageString);

      
    } catch (Exception e) {
      logger.error("Error while parsing the message: {}", e.getMessage());
    }
  }

  // This method is called when a Confirm message is received from the Inventory service
  public void receiveMessageConfirmed(String message) {
    logger.info("Received confirmation message: {}", message);
    try {
      ObjectMapper mapper = new ObjectMapper();
      MessageConfirmed confirmationMessage = mapper.readValue(message, MessageConfirmed.class);

      // Update the order status in the database
      Order order = orderRepository.findById(confirmationMessage.orderId).get();

      // Update the status of the order depending on the status of the confirmation message
      switch (confirmationMessage.status) {
        case "confirmed":
          order.status = OrderStatus.confirmed;
          break;
        case "paid":
          order.status = OrderStatus.paid;
          break;
        case "shipped":
          order.status = OrderStatus.shipped;
          break;
        default:
          logger.error("Error while parsing the Status of the order message: {}", confirmationMessage.status);
          break;
      }
      logger.info("Update the order status : {}", order.status);
      order = orderRepository.save(order);
      logger.info("Order saved : {}", order.toString());
    } catch (Exception e) {
      logger.error("Error while parsing the message: {}", e.getMessage());
    }
  }


}