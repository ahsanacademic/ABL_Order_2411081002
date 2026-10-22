package com.asan.order.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.asan.order.security.ServiceTokenProvider;
import com.asan.order.vo.Pelanggan;

@Service
public class PelangganClient {

  private final DiscoveryClient discoveryClient;
  private final ServiceTokenProvider serviceTokenProvider;
  private final RestTemplate restTemplate = new RestTemplate();

  public PelangganClient(DiscoveryClient discoveryClient, ServiceTokenProvider serviceTokenProvider) {
    this.discoveryClient = discoveryClient;
    this.serviceTokenProvider = serviceTokenProvider;
  }

  public Pelanggan findByAuthUserId(Long authUserId) {
    ServiceInstance instance = getPelangganInstance();
    try {
      return restTemplate.exchange(
          instance.getUri() + "/api/pelanggan/internal/by-auth-user/" + authUserId,
          HttpMethod.GET,
          new HttpEntity<>(serviceHeaders()),
          Pelanggan.class).getBody();
    } catch (HttpClientErrorException.NotFound exception) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
          "PROFILE_PENDING: profil pelanggan belum siap", exception);
    }
  }

  public Pelanggan findById(Long pelangganId) {
    ServiceInstance instance = getPelangganInstance();
    return restTemplate.exchange(
        instance.getUri() + "/api/pelanggan/" + pelangganId,
        HttpMethod.GET,
        new HttpEntity<>(serviceHeaders()),
        Pelanggan.class).getBody();
  }

  private ServiceInstance getPelangganInstance() {
    return discoveryClient.getInstances("PELANGGAN").stream()
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
            "Service Pelanggan tidak tersedia"));
  }

  private HttpHeaders serviceHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(serviceTokenProvider.createToken());
    return headers;
  }
}
