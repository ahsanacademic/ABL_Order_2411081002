package com.asan.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asan.order.readmodel.OrderView;
import com.asan.order.readmodel.OrderViewRepository;

@Service
public class OrderQueryService {

  @Autowired
  private OrderViewRepository orderViewRepository;

  @Transactional(value = "readTransactionManager", readOnly = true)
  public List<OrderView> getAllOrder() {
    return orderViewRepository.findAll();
  }

  @Transactional(value = "readTransactionManager", readOnly = true)
  public OrderView getOrderById(Long id) {
    return orderViewRepository.findById(id).orElse(null);
  }
}
