package com.asan.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.asan.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findAllByPelangganId(Long pelangganId);

  Optional<Order> findByIdAndPelangganId(Long id, Long pelangganId);
}
