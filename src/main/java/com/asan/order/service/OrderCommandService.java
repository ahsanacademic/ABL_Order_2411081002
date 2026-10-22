package com.asan.order.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.asan.order.entity.Order;
import com.asan.order.repository.OrderRepository;
import com.asan.order.security.ServiceTokenProvider;
import com.asan.order.vo.OrderMessage;
import com.asan.order.vo.Pelanggan;

@Service
public class OrderCommandService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private OrderMessagePublisher orderMessagePublisher;

  @Autowired
  private DiscoveryClient discoveryClient;

  @Autowired
  private ServiceTokenProvider serviceTokenProvider;

  private RestTemplate restTemplate = new RestTemplate();

  public Order createOrder(Order order) {
    if (order.getTglTransaksi() == null) {
      order.setTglTransaksi(LocalDate.now());
    }
    Order savedOrder = orderRepository.save(order);
    publishOrder(savedOrder);
    return savedOrder;
  }

  public Order updateOrder(Long id, Order order) {
    Order existingOrder = orderRepository.findById(id).orElse(null);
    if (existingOrder == null) {
      return null;
    }
    existingOrder.setProdukId(order.getProdukId());
    existingOrder.setPelangganId(order.getPelangganId());
    existingOrder.setJumlah(order.getJumlah());
    existingOrder.setTotal(order.getTotal());
    if (order.getTglTransaksi() != null) {
      existingOrder.setTglTransaksi(order.getTglTransaksi());
    }
    Order savedOrder = orderRepository.save(existingOrder);
    publishOrder(savedOrder);
    return savedOrder;
  }

  public boolean deleteOrder(Long id) {
    if (!orderRepository.existsById(id)) {
      return false;
    }
    orderRepository.deleteById(id);
    return true;
  }

  private void publishOrder(Order savedOrder) {
    Pelanggan pelanggan = getPelangganById(savedOrder.getPelangganId());
    orderMessagePublisher.publishOrder(new OrderMessage(savedOrder, pelanggan.getEmail()));
  }

  private Pelanggan getPelangganById(Long pelangganId) {
    ServiceInstance serviceInstancePelanggan = discoveryClient.getInstances("PELANGGAN").get(0);
    return restTemplate.exchange(
        serviceInstancePelanggan.getUri() + "/api/pelanggan/" + pelangganId,
        HttpMethod.GET,
        new HttpEntity<>(serviceHeaders()),
        Pelanggan.class).getBody();
  }

  private HttpHeaders serviceHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(serviceTokenProvider.createToken());
    return headers;
  }
}
