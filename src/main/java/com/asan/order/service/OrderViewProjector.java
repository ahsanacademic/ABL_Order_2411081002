package com.asan.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.asan.order.readmodel.OrderView;
import com.asan.order.readmodel.OrderViewRepository;
import com.asan.order.security.ServiceTokenProvider;
import com.asan.order.vo.OrderMessage;
import com.asan.order.vo.Pelanggan;
import com.asan.order.vo.Produk;

@Service
public class OrderViewProjector {

  @Autowired
  private OrderViewRepository orderViewRepository;

  @Autowired
  private DiscoveryClient discoveryClient;

  @Autowired
  private ServiceTokenProvider serviceTokenProvider;

  private RestTemplate restTemplate = new RestTemplate();

  @Transactional("readTransactionManager")
  public void sync(OrderMessage orderMessage) {
    OrderView orderView = orderViewRepository.findById(orderMessage.getId()).orElse(new OrderView());
    orderView.setId(orderMessage.getId());
    orderView.setProdukId(orderMessage.getProdukId());
    orderView.setPelangganId(orderMessage.getPelangganId());
    orderView.setEmailPelanggan(orderMessage.getEmail());
    orderView.setTglTransaksi(orderMessage.getTglTransaksi());
    orderView.setJumlah(orderMessage.getJumlah());
    orderView.setTotal(orderMessage.getTotal());

    Produk produk = getProdukById(orderMessage.getProdukId());
    if (produk != null) {
      orderView.setNamaProduk(produk.getNama());
    }

    Pelanggan pelanggan = getPelangganById(orderMessage.getPelangganId());
    if (pelanggan != null) {
      orderView.setNamaPelanggan(pelanggan.getNama());
    }

    orderViewRepository.save(orderView);
    System.out.println("Read DB synced for order id: " + orderView.getId());
  }

  private Produk getProdukById(Long produkId) {
    ServiceInstance serviceInstanceProduk = discoveryClient.getInstances("PRODUK").get(0);
    return restTemplate.exchange(
        serviceInstanceProduk.getUri() + "/api/produk/" + produkId,
        HttpMethod.GET,
        new HttpEntity<>(serviceHeaders()),
        Produk.class).getBody();
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
