package com.asan.order.readmodel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderViewRepository extends JpaRepository<OrderView, Long> {

  List<OrderView> findAllByPelangganId(Long pelangganId);

  Optional<OrderView> findByIdAndPelangganId(Long id, Long pelangganId);
}
