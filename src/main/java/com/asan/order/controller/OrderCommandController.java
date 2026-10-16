package com.asan.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asan.order.entity.Order;
import com.asan.order.service.OrderCommandService;

@RestController
@RequestMapping("/api/order/command")
public class OrderCommandController {

  @Autowired
  private OrderCommandService orderCommandService;

  @PostMapping
  public ResponseEntity<Order> createOrder(@RequestBody Order order) {
    return ResponseEntity.ok(orderCommandService.createOrder(order));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Order> updateOrder(@PathVariable("id") Long id, @RequestBody Order order) {
    Order updatedOrder = orderCommandService.updateOrder(id, order);
    if (updatedOrder == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(updatedOrder);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable("id") Long id) {
    if (!orderCommandService.deleteOrder(id)) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
  }
}
