package com.asan.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asan.order.client.PelangganClient;
import com.asan.order.readmodel.OrderView;
import com.asan.order.readmodel.OrderViewRepository;
import com.asan.order.vo.Pelanggan;

@Service
public class OrderQueryService {

  @Autowired
  private OrderViewRepository orderViewRepository;

  @Autowired
  private PelangganClient pelangganClient;

  @Transactional(value = "readTransactionManager", readOnly = true)
  public List<OrderView> getAllOrder(Long authUserId) {
    Pelanggan pelanggan = pelangganClient.findByAuthUserId(authUserId);
    return orderViewRepository.findAllByPelangganId(pelanggan.getId());
  }

  @Transactional(value = "readTransactionManager", readOnly = true)
  public OrderView getOrderById(Long id, Long authUserId) {
    Pelanggan pelanggan = pelangganClient.findByAuthUserId(authUserId);
    return orderViewRepository.findByIdAndPelangganId(id, pelanggan.getId()).orElse(null);
  }
}
