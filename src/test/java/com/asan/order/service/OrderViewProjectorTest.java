package com.asan.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.asan.order.client.PelangganClient;
import com.asan.order.client.ProdukClient;
import com.asan.order.readmodel.OrderView;
import com.asan.order.readmodel.OrderViewRepository;
import com.asan.order.vo.OrderMessage;
import com.asan.order.vo.Pelanggan;
import com.asan.order.vo.Produk;

@ExtendWith(MockitoExtension.class)
class OrderViewProjectorTest {

  @Mock
  private OrderViewRepository orderViewRepository;

  @Mock
  private ProdukClient produkClient;

  @Mock
  private PelangganClient pelangganClient;

  @InjectMocks
  private OrderViewProjector projector;

  @Test
  void createdEventProjectsItsDenormalizedFieldsWithoutCallingOtherServices() {
    OrderMessage event = event(OrderMessage.ORDER_CREATED);
    when(orderViewRepository.findById(101L)).thenReturn(Optional.empty());

    projector.sync(event);

    ArgumentCaptor<OrderView> viewCaptor = ArgumentCaptor.forClass(OrderView.class);
    verify(orderViewRepository).save(viewCaptor.capture());
    OrderView view = viewCaptor.getValue();
    assertEquals("Kopi Susu", view.getNamaProduk());
    assertEquals("Siti", view.getNamaPelanggan());
    assertEquals("siti@example.test", view.getEmailPelanggan());
    assertEquals(42L, view.getPelangganId());
    verify(produkClient, never()).findById(any());
    verify(pelangganClient, never()).findById(any());
  }

  @Test
  void deleteEventRemovesReadModelWithoutRequeryingOtherServices() {
    OrderMessage event = event(OrderMessage.ORDER_DELETED);

    projector.sync(event);

    verify(orderViewRepository).deleteById(101L);
    verify(orderViewRepository, never()).save(any(OrderView.class));
    verify(produkClient, never()).findById(any());
    verify(pelangganClient, never()).findById(any());
  }

  @Test
  void updatedEventOverwritesTheExistingReadModel() {
    OrderView existing = new OrderView();
    existing.setId(101L);
    existing.setNamaProduk("Nama lama");
    when(orderViewRepository.findById(101L)).thenReturn(Optional.of(existing));

    projector.sync(event(OrderMessage.ORDER_UPDATED));

    ArgumentCaptor<OrderView> viewCaptor = ArgumentCaptor.forClass(OrderView.class);
    verify(orderViewRepository).save(viewCaptor.capture());
    OrderView updated = viewCaptor.getValue();
    assertEquals("Kopi Susu", updated.getNamaProduk());
    assertEquals("Siti", updated.getNamaPelanggan());
    assertEquals(50_000.0, updated.getTotal());
  }

  @Test
  void legacyEventWithoutEventTypeStillProjectsAsCreated() {
    OrderMessage legacy = new OrderMessage();
    legacy.setId(101L);
    legacy.setProdukId(3L);
    legacy.setPelangganId(42L);
    legacy.setEmail("siti@example.test");
    legacy.setTglTransaksi(LocalDate.of(2026, 10, 22));
    legacy.setJumlah(2);
    legacy.setTotal(50_000);
    when(orderViewRepository.findById(101L)).thenReturn(Optional.empty());
    when(produkClient.findById(3L)).thenReturn(new Produk(3L, "Kopi Susu", 25_000, "Kopi", 1L));
    when(pelangganClient.findById(42L)).thenReturn(new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN"));

    projector.sync(legacy);

    ArgumentCaptor<OrderView> viewCaptor = ArgumentCaptor.forClass(OrderView.class);
    verify(orderViewRepository).save(viewCaptor.capture());
    assertEquals("Kopi Susu", viewCaptor.getValue().getNamaProduk());
    assertEquals("Siti", viewCaptor.getValue().getNamaPelanggan());
  }

  private OrderMessage event(String eventType) {
    OrderMessage event = new OrderMessage();
    event.setEventType(eventType);
    event.setId(101L);
    event.setProdukId(3L);
    event.setProdukNama("Kopi Susu");
    event.setProdukHarga(25_000.0);
    event.setPelangganId(42L);
    event.setPelangganNama("Siti");
    event.setPelangganAlamat("Alamat");
    event.setEmail("siti@example.test");
    event.setTglTransaksi(LocalDate.of(2026, 10, 22));
    event.setJumlah(2);
    event.setTotal(50_000);
    return event;
  }
}
