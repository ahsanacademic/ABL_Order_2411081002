package com.asan.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.asan.order.entity.Order;
import com.asan.order.repository.OrderRepository;
import com.asan.order.vo.Pelanggan;
import com.asan.order.vo.Produk;
import com.asan.order.vo.ResponseTemplate;

@Service
public class OrderQueryService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private DiscoveryClient discoveryClient;

  private RestTemplate restTemplate = new RestTemplate();

  public List<Order> getAllOrder() {
    return orderRepository.findAll();
  }

  public Order getOrderById(Long id) {
    return orderRepository.findById(id).orElse(null);
  }

  public List<ResponseTemplate> getOrderWithProdukById(Long orderId) {
    List<ResponseTemplate> responseList = new ArrayList<>();
    Order order = getOrderById(orderId);
    if (order == null) {
      return responseList;
    }

    ServiceInstance serviceInstanceProduk = discoveryClient.getInstances("PRODUK").get(0);
    ServiceInstance serviceInstancePelanggan = discoveryClient.getInstances("PELANGGAN").get(0);

    Produk produk = restTemplate.getForObject(serviceInstanceProduk.getUri() + "/api/produk/" + order.getProdukId(), Produk.class);
    Pelanggan pelanggan = restTemplate.getForObject(serviceInstancePelanggan.getUri() + "/api/pelanggan/" + order.getPelangganId(), Pelanggan.class);

    ResponseTemplate vo = new ResponseTemplate();
    vo.setOrder(order);
    vo.setProduk(produk);
    vo.setPelanggan(pelanggan);
    responseList.add(vo);
    return responseList;
  }
}
