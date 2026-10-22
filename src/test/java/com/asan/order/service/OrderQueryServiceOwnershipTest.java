package com.asan.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.asan.order.client.PelangganClient;
import com.asan.order.readmodel.OrderView;
import com.asan.order.readmodel.OrderViewRepository;
import com.asan.order.vo.Pelanggan;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceOwnershipTest {

  @Mock
  private OrderViewRepository orderViewRepository;

  @Mock
  private PelangganClient pelangganClient;

  @InjectMocks
  private OrderQueryService orderQueryService;

  @Test
  void listQueryOnlyLoadsAuthenticatedCustomersReadModels() {
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(
        new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN"));
    when(orderViewRepository.findAllByPelangganId(42L)).thenReturn(List.of(new OrderView()));

    List<OrderView> result = orderQueryService.getAllOrder(7L);

    assertEquals(1, result.size());
    verify(orderViewRepository).findAllByPelangganId(42L);
  }

  @Test
  void detailQueryScopesOrderIdToAuthenticatedCustomersProfile() {
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(
        new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN"));
    OrderView ownedOrder = new OrderView();
    when(orderViewRepository.findByIdAndPelangganId(12L, 42L)).thenReturn(Optional.of(ownedOrder));

    OrderView result = orderQueryService.getOrderById(12L, 7L);

    assertEquals(ownedOrder, result);
    verify(orderViewRepository).findByIdAndPelangganId(12L, 42L);
  }

  @Test
  void detailQueryTreatsAnotherCustomersOrderAsNotFound() {
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(
        new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN"));
    when(orderViewRepository.findByIdAndPelangganId(88L, 42L)).thenReturn(Optional.empty());

    assertNull(orderQueryService.getOrderById(88L, 7L));
    verify(orderViewRepository).findByIdAndPelangganId(88L, 42L);
  }
}
