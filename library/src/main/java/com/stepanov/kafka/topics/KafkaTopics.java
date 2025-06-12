package com.stepanov.kafka.topics;

public class KafkaTopics {

    public static final String GATEWAY_COMMAND_ORDER_TOPIC = "gateway.command-order.v1";
    public static final String GATEWAY_COMMAND_STOCK_TOPIC = "gateway.command-stock.v1";
    public static final String ORDER_COMMAND_STOCK_TOPIC = "order.command-stock.v1";

    public static final String ORDER_PREPARE_PAYMENT_TOPIC = "order.prepare-payment.v1";
    public static final String ORDER_ORDER_SYNC_TOPIC = "order.order-sync.v1";

    public static final String STOCK_RESERVATION_STATUS_TOPIC = "stock.reservation-status.v1";
    public static final String STOCK_PRODUCT_SYNC_TOPIC = "stock.product-sync.v1";

    public static final String PAYMENT_NOTIFICATION_TOPIC = "payment.notification.v1";
}
