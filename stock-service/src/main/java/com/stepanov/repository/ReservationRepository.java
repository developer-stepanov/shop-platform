package com.stepanov.repository;

import com.stepanov.entity.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationItem, Long> {

    ReservationItem findByOrderIdAndSku(UUID orderId, String sku);
}
