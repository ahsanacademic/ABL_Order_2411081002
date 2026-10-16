package com.asan.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.asan.order.entity.Order;
import com.asan.order.service.OrderQueryService;
import com.asan.order.vo.ResponseTemplate;

@RestController
@RequestMapping("/api/order/query")
public class OrderQueryController {

  @Autowired
  private OrderQueryService orderQueryService;

  @GetMapping
  public List<Order> getAllOrder() {
    return orderQueryService.getAllOrder();
  }

  @GetMapping("/{id}")
  public ResponseEntity<List<ResponseTemplate>> getOrderWithProdukById(@PathVariable("id") Long id) {
    List<ResponseTemplate> responseList = orderQueryService.getOrderWithProdukById(id);
    if (responseList.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(responseList);
  }
}
