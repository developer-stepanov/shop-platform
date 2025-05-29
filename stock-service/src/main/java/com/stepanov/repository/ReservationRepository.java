package com.stepanov.repository;

import com.stepanov.entity.ReservationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationItemEntity, Long> {

    ReservationItemEntity findByOrderIdAndSku(UUID orderId, String sku);
}
