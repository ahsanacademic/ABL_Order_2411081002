package com.asan.order.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.asan.order.client.PelangganClient;
import com.asan.order.client.ProdukClient;
import com.asan.order.readmodel.OrderView;
import com.asan.order.readmodel.OrderViewRepository;
import com.asan.order.vo.OrderMessage;
import com.asan.order.vo.Pelanggan;
import com.asan.order.vo.Produk;

@Service
public class OrderViewProjector {

  private final OrderViewRepository orderViewRepository;
  private final ProdukClient produkClient;
  private final PelangganClient pelangganClient;

  public OrderViewProjector(OrderViewRepository orderViewRepository, ProdukClient produkClient,
      PelangganClient pelangganClient) {
    this.orderViewRepository = orderViewRepository;
    this.produkClient = produkClient;
    this.pelangganClient = pelangganClient;
  }

  @Transactional("readTransactionManager")
  public void sync(OrderMessage orderMessage) {
    if (orderMessage == null || orderMessage.getId() == null) {
      throw new IllegalArgumentException("Order event must contain an order id");
    }

    String eventType = orderMessage.getEventType() == null
        ? OrderMessage.ORDER_CREATED
        : orderMessage.getEventType();
    if (OrderMessage.ORDER_DELETED.equals(eventType)) {
      orderViewRepository.deleteById(orderMessage.getId());
      System.out.println("Read DB removed for order id: " + orderMessage.getId());
      return;
    }
    if (!OrderMessage.ORDER_CREATED.equals(eventType)
        && !OrderMessage.ORDER_UPDATED.equals(eventType)) {
      throw new IllegalArgumentException("Unsupported order event type");
    }

    OrderView orderView = orderViewRepository.findById(orderMessage.getId()).orElseGet(OrderView::new);
    orderView.setId(orderMessage.getId());
    orderView.setProdukId(orderMessage.getProdukId());
    orderView.setPelangganId(orderMessage.getPelangganId());
    orderView.setEmailPelanggan(orderMessage.getEmail());
    orderView.setTglTransaksi(orderMessage.getTglTransaksi());
    orderView.setJumlah(orderMessage.getJumlah());
    orderView.setTotal(orderMessage.getTotal());
    orderView.setNamaProduk(resolveProductName(orderMessage));
    orderView.setNamaPelanggan(resolveCustomerName(orderMessage));

    orderViewRepository.save(orderView);
    System.out.println("Read DB synced for order event " + eventType + " id=" + orderView.getId());
  }

  private String resolveProductName(OrderMessage event) {
    if (hasText(event.getProdukNama())) {
      return event.getProdukNama();
    }
    Produk produk = produkClient.findById(event.getProdukId());
    return produk == null ? null : produk.getNama();
  }

  private String resolveCustomerName(OrderMessage event) {
    if (hasText(event.getPelangganNama())) {
      return event.getPelangganNama();
    }
    Pelanggan pelanggan = pelangganClient.findById(event.getPelangganId());
    return pelanggan == null ? null : pelanggan.getNama();
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
