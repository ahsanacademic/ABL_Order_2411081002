package com.asan.order.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.order.entity.Order;
import com.asan.order.repository.OrderRepository;

@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  public List<Order> getAllOrder() {
    return orderRepository.findAll();
  }

  public Order getOrderById(Long id) {
    return orderRepository.findById(id).orElse(null);
  }

  public Order saveOrder(Order order) {
    if (order.getTglTransaksi() == null) {
      order.setTglTransaksi(LocalDate.now());
    }
    return orderRepository.save(order);
  }
}