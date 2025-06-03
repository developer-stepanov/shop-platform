package com.stepanov.service;

import com.stepanov.entity.ReservationItemEntity;
import com.stepanov.entity.StockItemEntity;

import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderStatus;
import com.stepanov.enums.ReservationStatus;

import com.stepanov.exceptions.OutOfStockException;

import com.stepanov.kafka.events.*;

import com.stepanov.messaging.StockEventsPublisher;

import com.stepanov.repository.ReservationRepository;
import com.stepanov.repository.StockRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class StockService {

    private final StockEventsPublisher publisher;

    private final StockRepository stockRepository;

    private final ReservationRepository reservationRepository;

    @Transactional
    public void reserveBy(OrderForStock evt) {
        List<ConfirmationReservation.UnitPrice> unitPrices = new ArrayList<>();
        PaymentDetails paymentDetails;

        evt.orderItems().forEach(it -> {
           // Pessimistic lock – prevents oversell
           StockItemEntity stockItem = stockRepository.lockStockItemBySku(it.sku());

           final int itemsToReserve = it.qty();

           if (stockItem.getAvailableQty() < itemsToReserve) {
               publisher.publishOutOfStock(OutOfStock.builder().orderId(evt.orderId()).build());
               String exceptionMsg = String.format("%s has less then %s items", stockItem.getSku(), itemsToReserve);
               throw new OutOfStockException(exceptionMsg);
           }

           stockItem.decreaseAvailableQty(itemsToReserve);
           stockRepository.save(stockItem); // to run StockItemDomainEventListener

           ReservationItemEntity reservation = ReservationItemEntity.builder()
                                                                   .orderId(evt.orderId())
                                                                   .sku(it.sku())
                                                                   .qty(new BigDecimal(itemsToReserve))
                                                                   .reservationStatus(ReservationStatus.RESERVED)
                                                                   .build();

           unitPrices.add(ConfirmationReservation.UnitPrice.builder()
                                        .sku(stockItem.getSku())
                                        .unitPrice(stockItem.getUnitPrice())
                                        .build());
           reservationRepository.save(reservation);
        });

        paymentDetails = PaymentDetails.builder()
                                    .totalPayment(unitPrices.stream()
                                                    .map(ConfirmationReservation.UnitPrice::unitPrice)
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add))
                                    .currency(Currency.EUR)
                                    .build();

        publisher.publishReservedOrder(ConfirmationReservation.builder()
                                                    .orderId(evt.orderId())
                                                    .orderStatus(OrderStatus.RESERVED)
                                                    .paymentDetails(paymentDetails)
                                                    .unitPrices(unitPrices)
                                                    .build());
    }

    @Transactional
    public void releaseStockBy(StockRelease evt) {
        evt.orderItems().forEach(it -> {
            // Pessimistic lock ?? do i need it?
            StockItemEntity stockItem = stockRepository.lockStockItemBySku(it.sku());

            final int itemsToRelease = it.qty();

            stockItem.increaseAvailableQty(itemsToRelease);
            stockRepository.save(stockItem);

            ReservationItemEntity reservationItem = reservationRepository.findByOrderIdAndSku(evt.orderId(), it.sku());
            reservationItem.setReservationStatus(ReservationStatus.RELEASED);
        });
    }
}
