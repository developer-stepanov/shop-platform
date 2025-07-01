package com.stepanov.service;

import com.stepanov.entity.ReservationItem;
import com.stepanov.entity.StockItem;
import com.stepanov.enums.Currency;
import com.stepanov.enums.OrderStatus;
import com.stepanov.enums.ReservationStatus;
import com.stepanov.exceptions.OutOfStockException;
import com.stepanov.kafka.events.topics.orders.OrderForStock;
import com.stepanov.kafka.events.topics.orders.OrderItem;
import com.stepanov.kafka.events.topics.orders.StockRelease;
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation;
import com.stepanov.kafka.events.topics.stock.ItemsForSell;
import com.stepanov.kafka.events.topics.stock.OutOfStock;
import com.stepanov.kafka.events.topics.stock.PaymentDetails;
import com.stepanov.mapper.StockMapper;
import com.stepanov.messaging.publisher.StockPublisher;
import com.stepanov.repository.ReservationRepository;
import com.stepanov.repository.StockRepository;
import com.stepanov.repository.manager.ReservationManager;
import com.stepanov.repository.manager.StockManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class StockService {

    private final StockPublisher publisher;

    private final StockManager stockManager;

    private final ReservationManager reservationManager;

    private final StockRepository stockRepository;

    private final ReservationRepository reservationRepository;

    public void itemsForSell() {
        final List<StockItem> stockEntities = stockRepository.findAll();
        final ItemsForSell items = StockMapper.fromStockItems(stockEntities);

        publisher.publish(items);
    }

    @Transactional
    public void reserveBy(@NonNull OrderForStock evt) {
        final UUID orderId = evt.orderId();
        final List<ConfirmationReservation.UnitPrice> unitPrices = new ArrayList<>();

        for (OrderItem orderItem : evt.orderItems()) {
            handleStockReservation(orderItem, evt, unitPrices);
        }

        publishConfirmation(orderId,
                            buildPaymentDetail(unitPrices),
                            unitPrices);

        log.info("Successfully reserved stock for order [{}] with [{}] items. Confirmation published.",
                orderId, evt.orderItems().size());
    }

    @Transactional
    public void releaseStockBy(@NonNull StockRelease evt) {
        final UUID orderId = evt.orderId();

        for (OrderItem orderItem : evt.orderItems()) {
            releaseStock(orderItem.sku(), orderItem.qty());
            updateReservationStatus(orderId, orderItem.sku());
        }

        log.info("Successfully released stock for order [{}] with [{}] items.", orderId, evt.orderItems().size());
    }

    private void handleStockReservation(@NonNull OrderItem orderItem,
                                        @NonNull OrderForStock evt,
                                        @NonNull List<ConfirmationReservation.UnitPrice> unitPrices) {

        final StockItem stockItem = stockManager.getRequiredLockedStockItemBySku(orderItem.sku());
        final int qtyToReserve = orderItem.qty();

        if (qtyToReserve > stockItem.getAvailableQty()) {
            publisher.publish(OutOfStock.builder().orderId(evt.orderId()).build());
            throw new OutOfStockException(String.format("Stock item SKU: %s has less then %s items",
                                                        stockItem.getSku(),
                                                        qtyToReserve));
        }

        stockItem.decreaseAvailableQty(qtyToReserve);
        stockRepository.save(stockItem);

        saveReservation(evt.orderId(), orderItem.sku(), qtyToReserve);
        unitPrices.add(buildUnitPrice(stockItem));
    }

    private void saveReservation(@NonNull UUID orderId, @NonNull String sku, int qty) {
        final ReservationItem reservation = ReservationItem.builder()
                                                        .orderId(orderId)
                                                        .sku(sku)
                                                        .qty(new BigDecimal(qty))
                                                        .reservationStatus(ReservationStatus.RESERVED)
                                                        .build();
        reservationRepository.save(reservation);
    }

    private ConfirmationReservation.UnitPrice buildUnitPrice(@NonNull StockItem stockItem) {
        return new ConfirmationReservation.UnitPrice(stockItem.getSku(), stockItem.getUnitPrice());
    }

    private PaymentDetails buildPaymentDetail(@NonNull List<ConfirmationReservation.UnitPrice> unitPrices) {
        final BigDecimal totalAmount = unitPrices.stream()
                                            .map(ConfirmationReservation.UnitPrice::unitPrice)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentDetails(totalAmount, Currency.EUR);
    }

    private void publishConfirmation(@NonNull UUID orderId,
                                     @NonNull PaymentDetails paymentDetails,
                                     @NonNull List<ConfirmationReservation.UnitPrice> unitPrices) {

        publisher.publish(ConfirmationReservation.builder()
                                            .orderId(orderId)
                                            .orderStatus(OrderStatus.RESERVED)
                                            .paymentDetails(paymentDetails)
                                            .unitPrices(unitPrices)
                                            .build());
    }

    private void releaseStock(@NonNull String sku, int qty) {
        final StockItem stockItem = stockManager.getRequiredLockedStockItemBySku(sku);
        stockItem.increaseAvailableQty(qty);
        stockRepository.save(stockItem);
    }

    private void updateReservationStatus(@NonNull UUID orderId, @NonNull String sku) {
        final ReservationItem reservationItem = reservationManager.getRequiredReservationItem(orderId, sku);
        reservationItem.setReservationStatus(ReservationStatus.RELEASED);
    }

}
