package com.asan.order.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.asan.order.security.ServiceTokenProvider;
import com.asan.order.vo.Produk;

@Service
public class ProdukClient {

  private final DiscoveryClient discoveryClient;
  private final ServiceTokenProvider serviceTokenProvider;
  private final RestTemplate restTemplate = new RestTemplate();

  public ProdukClient(DiscoveryClient discoveryClient, ServiceTokenProvider serviceTokenProvider) {
    this.discoveryClient = discoveryClient;
    this.serviceTokenProvider = serviceTokenProvider;
  }

  public Produk findById(Long produkId) {
    ServiceInstance instance = discoveryClient.getInstances("PRODUK").stream()
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
            "Service Produk tidak tersedia"));
    return restTemplate.exchange(
        instance.getUri() + "/api/produk/" + produkId,
        HttpMethod.GET,
        new HttpEntity<>(serviceHeaders()),
        Produk.class).getBody();
  }

  private HttpHeaders serviceHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(serviceTokenProvider.createToken());
    return headers;
  }
}
