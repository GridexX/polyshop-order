package fr.dopolytech.polyshop.order.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.order.messages.ErrorMessage;
import fr.dopolytech.polyshop.order.messages.InventoryMessage;
import fr.dopolytech.polyshop.order.messages.ShoppingCartMessage;
import fr.dopolytech.polyshop.order.models.Order;
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

  // This method is called when a message is received from the Shopping Card service
  public void receiveMessage(String message) {
    logger.info("Received message: {}", message);

    // Receive a message from the Shopping Card service and handle it

    try {
      ObjectMapper mapper = new ObjectMapper();
      ShoppingCartMessage shoppingCartMessage = mapper.readValue(message, ShoppingCartMessage.class);

      // Save the order and the products in the database
      Order order = orderRepository.save(new Order());

      shoppingCartMessage.products.forEach(product -> {
        Product productToSave = new Product( product.amount, order.id);
        productRepository.save(productToSave);
      });
      
      // Transfer the message to the Inventory service
      InventoryMessage inventoryMessage = new InventoryMessage(order.id, shoppingCartMessage.products);
      String inventoryMessageString = mapper.writeValueAsString(inventoryMessage);
      rabbitTemplate.convertAndSend(inventoryQueue.getName(), inventoryMessageString);

      
    } catch (Exception e) {
      logger.error("Error while parsing the message: {}", e.getMessage());
    }
  }


  // This method is called when a Cancel message is received from the Inventory service
  public void receiveMessageCancel(String message) {
    logger.info("Received message: {}", message);

    // Receive a message from the Inventory service and handle them

    try {
      ObjectMapper mapper = new ObjectMapper();
      ErrorMessage errorMessage = mapper.readValue(message, ErrorMessage.class);

      // Cancel the order in the database
      Order order = orderRepository.findById(errorMessage.orderId).get();

      // Find all products related to the order by searching the order id
      productRepository.findAll().forEach(product -> {
        if (product.getOrderId() == order.id) {
          productRepository.deleteById(product.getId());
        }
      });

      // Remove the order
      orderRepository.delete(order);

      // Transfer the message to the Shopping Card service
      String errorMessageString = mapper.writeValueAsString(errorMessage);
      rabbitTemplate.convertAndSend(orderCancelQueue.getName(), errorMessageString);

      
    } catch (Exception e) {
      logger.error("Error while parsing the message: {}", e.getMessage());
    }
  }

}