package com.asan.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asan.order.entity.Order;
import com.asan.order.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

  @Autowired
  private OrderService orderService;

  @GetMapping
  public List<Order> getAllOrder() {
    return orderService.getAllOrder();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
    Order order = orderService.getOrderById(id);
    if (order != null) {
      return ResponseEntity.ok(order);
    }
    return ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<Order> createOrder(@RequestBody Order order) {
    return ResponseEntity.ok(orderService.saveOrder(order));
  }
}