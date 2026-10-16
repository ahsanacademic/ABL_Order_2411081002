package com.asan.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asan.order.readmodel.OrderView;
import com.asan.order.service.OrderQueryService;

@RestController
@RequestMapping("/api/order/query")
public class OrderQueryController {

  @Autowired
  private OrderQueryService orderQueryService;

  @GetMapping
  public List<OrderView> getAllOrder() {
    return orderQueryService.getAllOrder();
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderView> getOrderById(@PathVariable("id") Long id) {
    OrderView orderView = orderQueryService.getOrderById(id);
    if (orderView == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(orderView);
  }
}
