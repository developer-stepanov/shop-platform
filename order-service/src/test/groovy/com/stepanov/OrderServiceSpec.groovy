package com.stepanov

import com.stepanov.enums.Currency
import com.stepanov.enums.OrderStatus
import com.stepanov.kafka.events.topics.orders.CreateOrder
import com.stepanov.kafka.events.topics.orders.OrderItem
import com.stepanov.repository.OrderRepository
import com.stepanov.service.OrderService
import spock.lang.Specification
import spock.lang.Subject

class OrderServiceSpec extends Specification {

    final static String SKU_TEST_VALUE = 'SKU-1000'

    final static int QTY_TEST_VALUE = 1

    OrderRepository orderRepository = Mock()

    @Subject
    OrderService orderService = new OrderService(orderRepository)

    void 'should save order with correct values when creating a new order'() {
        given: 'a create order event with one order item'
            var createOrderEvent = buildCreateOrderEvent()
        when: 'the service creates a new order'
            orderService.createOrder(createOrderEvent)
        then: 'the repository is called to save an order with expected fields'
            1 * orderRepository.save({
                it.status == OrderStatus.CREATED
                it.totalAmount == BigDecimal.ZERO
                it.currency == Currency.EUR
                it.items.size() == 1
                it.items.first().sku == SKU_TEST_VALUE
                it.items.first().qty == QTY_TEST_VALUE
            }) >> { it[0] }
    }

    void 'should throw NullPointerException and not save when CreateOrder event is null'() {
        given: 'a null CreateOrder event'
            var createOrderEvent = null
        when: 'createOrder is called with the null event'
            orderService.createOrder(createOrderEvent)
        then: 'a NullPointerException is thrown'
            thrown NullPointerException
        and: 'the repository save method is never called'
            0 * orderRepository.save(_)
    }

    void 'should throw NullPointerException when CreateOrder is constructed with null orderItems'() {
        when: 'CreateOrder is instantiated with null orderItems'
            new CreateOrder(null)
        then: 'a NullPointerException is thrown'
            thrown NullPointerException
    }

    private static CreateOrder buildCreateOrderEvent() {
        CreateOrder.builder().orderItems(List.of(OrderItem.builder()
                                                        .qty(QTY_TEST_VALUE)
                                                        .sku(SKU_TEST_VALUE)
                                                        .build())).build()
    }
}
