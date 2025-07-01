package com.stepanov.repository.manager;

import com.stepanov.entity.ReservationItem;
import com.stepanov.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReservationManager {

    private final ReservationRepository reservationRepository;

    public ReservationItem getRequiredReservationItem(@NonNull UUID orderId, @NonNull String sku) {
        return reservationRepository.findByOrderIdAndSku(orderId, sku)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Reservation item not found for ORDER_ID: %s and SKU: %s",
                                orderId, sku))
                );
    }
}
