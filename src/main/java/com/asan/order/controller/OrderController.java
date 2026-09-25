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
import com.asan.order.vo.ResponseTemplate;

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
  public ResponseEntity<List<ResponseTemplate>> getOrderEntityById(@PathVariable("id") Long id) {
    List<ResponseTemplate> responseList = orderService.getOrderWithProdukById(id);
    return ResponseEntity.ok(responseList);
  }

  @PostMapping
  public ResponseEntity<Order> createOrder(@RequestBody Order order) {
    return ResponseEntity.ok(orderService.saveOrder(order));
  }
}