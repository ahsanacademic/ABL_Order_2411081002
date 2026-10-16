package com.asan.order.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.order.config.RabbitMQConfig;
import com.asan.order.vo.OrderMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrderSyncListener {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private OrderViewProjector orderViewProjector;

  @RabbitListener(queues = RabbitMQConfig.ORDER_SYNC_QUEUE_NAME)
  public void receiveOrder(String message) {
    try {
      OrderMessage orderMessage = objectMapper.readValue(message, OrderMessage.class);
      orderViewProjector.sync(orderMessage);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to read order message", e);
    }
  }
}
