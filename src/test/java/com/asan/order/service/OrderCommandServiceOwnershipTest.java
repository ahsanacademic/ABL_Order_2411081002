package com.asan.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.asan.order.entity.Order;
import com.asan.order.repository.OrderRepository;
import com.asan.order.client.PelangganClient;
import com.asan.order.vo.Pelanggan;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceOwnershipTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderMessagePublisher orderMessagePublisher;

  @Mock
  private PelangganClient pelangganClient;

  @InjectMocks
  private OrderCommandService orderCommandService;

  @Test
  void createUsesAuthenticatedCustomersProfileInsteadOfBodyPelangganId() {
    Pelanggan owner = new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN");
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(owner);
    when(pelangganClient.findById(42L)).thenReturn(owner);
    AtomicReference<Long> idAtSave = new AtomicReference<>();
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      idAtSave.set(order.getId());
      order.setId(100L);
      return order;
    });
    Order request = new Order(999L, 3L, 999L, null, 2, 200.0);

    Order created = orderCommandService.createOrder(request, 7L);

    assertEquals(100L, created.getId());
    assertEquals(42L, created.getPelangganId());
    assertNull(idAtSave.get());
    assertEquals(42L, request.getPelangganId());
    verify(pelangganClient).findByAuthUserId(7L);
    verify(orderMessagePublisher).publishOrder(any());
  }

  @Test
  void updateCannotModifyAnOrderOwnedByAnotherCustomer() {
    Pelanggan currentCustomer = new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN");
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(currentCustomer);
    when(orderRepository.findByIdAndPelangganId(900L, 42L)).thenReturn(Optional.empty());

    Order updated = orderCommandService.updateOrder(900L, 7L, new Order());

    assertNull(updated);
    verify(orderRepository, never()).save(any(Order.class));
    verify(orderMessagePublisher, never()).publishOrder(any());
  }

  @Test
  void updateKeepsExistingOwnerEvenWhenBodyContainsAnotherPelangganId() {
    Pelanggan owner = new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN");
    Order existing = new Order(900L, 3L, 42L, null, 1, 100.0);
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(owner);
    when(pelangganClient.findById(42L)).thenReturn(owner);
    when(orderRepository.findByIdAndPelangganId(900L, 42L)).thenReturn(Optional.of(existing));
    when(orderRepository.save(existing)).thenReturn(existing);

    Order request = new Order(null, 4L, 999L, null, 2, 200.0);
    Order updated = orderCommandService.updateOrder(900L, 7L, request);

    assertEquals(4L, updated.getProdukId());
    assertEquals(42L, updated.getPelangganId());
    verify(orderRepository).findByIdAndPelangganId(900L, 42L);
    verify(orderMessagePublisher).publishOrder(any());
  }

  @Test
  void deleteCannotRemoveAnOrderOwnedByAnotherCustomer() {
    Pelanggan currentCustomer = new Pelanggan(42L, "Siti", "siti@example.test", "Alamat", "PEREMPUAN");
    when(pelangganClient.findByAuthUserId(7L)).thenReturn(currentCustomer);
    when(orderRepository.findByIdAndPelangganId(900L, 42L)).thenReturn(Optional.empty());

    boolean deleted = orderCommandService.deleteOrder(900L, 7L);

    assertEquals(false, deleted);
    verify(orderRepository, never()).delete(any(Order.class));
  }
}
