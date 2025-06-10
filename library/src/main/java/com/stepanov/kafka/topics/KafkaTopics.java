package com.stepanov.kafka.topics;

public class KafkaTopics {

    public static final String GATEWAY_COMMAND_CREATE_ORDER_TOPIC = "gateway.command-create-order.v1";
    public static final String GATEWAY_COMMAND_FETCH_ORDERS_TOPIC = "gateway.command-fetch-orders.v1";

    public static final String ORDER_FETCHED_ORDERS_TOPIC = "order.fetched-orders.v1";


    public static final String ORDER_RESERVE_ORDER_TOPIC = "order.reserve-order.v1";


    // +
    public static final String ORDER_ORDER_ACCEPTED_TOPIC = "order.order-accepted.v1";
    // +
    public static final String ORDER_ORDER_UPDATED_TOPIC = "order.order-updated.v1";


    //+
    public static final String STOCK_RESERVATION_STATUS_TOPIC = "stock.reservation-status.v1";


    // +
    public static final String ORDER_RELEASE_STOCK_TOPIC = "order.release-stock.v1";
    // +
    public static final String STOCK_SKU_QTY_UPDATE_TOPIC = "stock.sku-qty-update.v1";
    // +
    public static final String ORDER_NOTIFY_PAYMENT_TOPIC = "order.notify-payment.v1";
    // +
    public static final String PAYMENT_CHECKOUT_PAYMENT_LINK_TOPIC = "payment.checkout-payment-link.v1";
    // +
    public static final String PAYMENT_PAYMENT_STATUS_TOPIC = "payment.payment-status.v1";


    // -
    public static final String GATEWAY_COMMAND_FETCH_PRODUCTS_TOPIC = "gateway.command-fetch-products.v1";
    // -
    public static final String STOCK_FETCHED_PRODUCTS_TOPIC = "stock.fetched-products.v1";


    // NOT USED
    public static final String ORDER_CANCELLED_TOPIC = "order-cancelled.v1";
    public static final String ORDER_PRICE_UPDATED_TOPIC = "order-price-updated.v1";
}
