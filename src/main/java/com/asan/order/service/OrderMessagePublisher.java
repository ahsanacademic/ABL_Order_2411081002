package com.asan.order.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.order.config.RabbitMQConfig;
import com.asan.order.vo.OrderMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderMessagePublisher {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  public void publishOrder(OrderMessage orderMessage) {
    try {
      String message = objectMapper.writeValueAsString(orderMessage);
      rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE_NAME, "", message);
      System.out.println("Order event published: type=" + orderMessage.getEventType() + " id=" + orderMessage.getId());
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to publish order message", e);
    }
  }
}
