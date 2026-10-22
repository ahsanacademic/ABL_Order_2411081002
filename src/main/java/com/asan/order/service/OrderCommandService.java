package com.asan.order.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asan.order.client.PelangganClient;
import com.asan.order.entity.Order;
import com.asan.order.repository.OrderRepository;
import com.asan.order.vo.OrderMessage;
import com.asan.order.vo.Pelanggan;

@Service
public class OrderCommandService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderMessagePublisher orderMessagePublisher;

  @Autowired
  private PelangganClient pelangganClient;

  public Order createOrder(Order order, Long authUserId) {
    Pelanggan pelanggan = pelangganClient.findByAuthUserId(authUserId);
    order.setId(null);
    order.setPelangganId(pelanggan.getId());
    if (order.getTglTransaksi() == null) {
      order.setTglTransaksi(LocalDate.now());
    }
    Order savedOrder = orderRepository.save(order);
    publishOrder(savedOrder);
    return savedOrder;
  }

  public Order updateOrder(Long id, Long authUserId, Order order) {
    Pelanggan pelanggan = pelangganClient.findByAuthUserId(authUserId);
    Order existingOrder = orderRepository.findByIdAndPelangganId(id, pelanggan.getId()).orElse(null);
    if (existingOrder == null) {
      return null;
    }
    existingOrder.setProdukId(order.getProdukId());
    existingOrder.setJumlah(order.getJumlah());
    existingOrder.setTotal(order.getTotal());
    if (order.getTglTransaksi() != null) {
      existingOrder.setTglTransaksi(order.getTglTransaksi());
    }
    Order savedOrder = orderRepository.save(existingOrder);
    publishOrder(savedOrder);
    return savedOrder;
  }

  public boolean deleteOrder(Long id, Long authUserId) {
    Pelanggan pelanggan = pelangganClient.findByAuthUserId(authUserId);
    Order existingOrder = orderRepository.findByIdAndPelangganId(id, pelanggan.getId()).orElse(null);
    if (existingOrder == null) {
      return false;
    }
    orderRepository.delete(existingOrder);
    return true;
  }

  private void publishOrder(Order savedOrder) {
    Pelanggan pelanggan = pelangganClient.findById(savedOrder.getPelangganId());
    orderMessagePublisher.publishOrder(new OrderMessage(savedOrder, pelanggan.getEmail()));
  }
}
