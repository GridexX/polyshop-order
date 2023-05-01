package fr.dopolytech.polyshop.order.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.dopolytech.polyshop.order.services.OrderService;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

@Configuration
public class RabbitMqConfig {

  // Queue used to listen to the shopping card service message
  @Bean
  public Queue orderQueue() {
    return new Queue("order", false);
  }

  // Queue used to send the order cancel message
  @Bean
  public Queue orderCancelQueue() {
    return new Queue("order_cancel", false);
  }

  // Queue used to listen to the inventory service cancel message
  @Bean
  public Queue inventoryCancelQueue() {
    return new Queue("inventory_cancel", false);
  }

  // Queue used to listen to the inventory service confirmed message
  @Bean
  public Queue inventoryConfirmedQueue() {
    return new Queue("inventory_confirmed", false);
  }

  // Queue used to send the inventory service message
  @Bean
  public Queue inventoryQueue() {
    return new Queue("inventory", false);
  }

  @Bean
  public MessageListenerAdapter listenerAdapter(OrderService orderService) {
    return new MessageListenerAdapter(orderService, "receiveMessage");
  }

  @Bean
  public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames("order");
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean 
  public MessageListenerAdapter listenerAdapterCancel(OrderService orderService) {
    return new MessageListenerAdapter(orderService, "receiveMessageCancel");
  }

  @Bean
  public SimpleMessageListenerContainer containerCancel(ConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapterCancel) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames("inventory_cancel");
    container.setMessageListener(listenerAdapterCancel);
    return container;
  }

  @Bean 
  public MessageListenerAdapter listenerAdapterConfirmed(OrderService orderService) {
    return new MessageListenerAdapter(orderService, "receiveMessageConfirmed");
  }

  @Bean
  public SimpleMessageListenerContainer containerConfirmed(ConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapterConfirmed) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames("inventory_confirmed");
    container.setMessageListener(listenerAdapterConfirmed);
    return container;
  }

}