package com.stepanov.repository;

import com.stepanov.entity.ReservationItem;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<ReservationItem, Long> {

    Optional<ReservationItem> findByOrderIdAndSku(@NonNull UUID orderId, @NonNull String sku);
}
