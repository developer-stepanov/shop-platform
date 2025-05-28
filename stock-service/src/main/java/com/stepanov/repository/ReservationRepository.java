package com.stepanov.repository;

import com.stepanov.entity.ReservationItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<ReservationItemEntity, Long> {
}
