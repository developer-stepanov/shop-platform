package com.stepanov

import com.stepanov.entity.Payment
import com.stepanov.enums.Currency
import com.stepanov.enums.OrderStatus
import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation
import com.stepanov.kafka.events.topics.stock.PaymentDetails
import com.stepanov.messaging.listener.PaymentListener
import com.stepanov.repository.PaymentRepository
import nl.altindag.log.LogCaptor
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_PREPARE_PAYMENT_TOPIC
import static com.stepanov.kafka.topics.KafkaTopics.PAYMENT_NOTIFICATION_TOPIC

@EmbeddedKafka(kraft = true,
                partitions = 1,
                topics = [ORDER_PREPARE_PAYMENT_TOPIC, PAYMENT_NOTIFICATION_TOPIC])
class PaymentListenerIntegrationSpec extends AbstractIntegrationTests {

    final String ORDER_ID = '9f000021-976a-1676-8197-6ac852ab0001'

    static final String SKU = 'SKU-1000'

    static final BigDecimal UNIT_PRICE = 1000

    LogCaptor logCaptor

    @Autowired
    PaymentListener paymentListener

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    ConsumerFactory<String, CheckoutPaymentLink> consumerFactory

    Consumer<String, CheckoutPaymentLink> consumer

    void setup() {
        logCaptor = LogCaptor.forClass(PaymentListener)
        consumer = consumerFactory.createConsumer()
        consumer.assign([new TopicPartition(PAYMENT_NOTIFICATION_TOPIC, 0)])
    }

    void 'should persist Payment and emit checkout link on ConfirmationReservation'() {
        given: 'ConfirmationReservation event'
            var evt = validEvent()
        when: 'the payment listener consumes it'
            paymentListener.on(evt, evt.orderId().toString())
        and: 'consume the emitted Kafka record'
            var checkoutPaymentLinkConsumerRecord =
                    KafkaTestUtils.getSingleRecord(consumer, PAYMENT_NOTIFICATION_TOPIC)
        then: 'the Payment entity is persisted'
            paymentRepository.count() == 1
        and: 'the persisted entity has correct fields'
            with(paymentRepository.findAll().first()) {
                orderId.toString() == ORDER_ID
                stripeCheckoutUrl
            }
        and: 'the Kafka notification contains correct payload'
            with(checkoutPaymentLinkConsumerRecord.value()) {
                orderId().toString() == ORDER_ID
                checkoutUrl()
            }
        and: "valid log from ${PaymentListener} logged"
            Payment paymentItem = paymentRepository.findAll().first()
            logCaptor.infoLogs.any {
                "Prepared payment [${paymentItem.id}] " +
                    "for order [${evt.orderId()}], " +
                    "Stripe session [${paymentItem.stripeSessionId}] created."
            }
    }

    private ConfirmationReservation validEvent() {
        ConfirmationReservation.builder()
                .orderId(UUID.fromString(ORDER_ID))
                .orderStatus(OrderStatus.RESERVED)
                .paymentDetails(new PaymentDetails(UNIT_PRICE, Currency.EUR))
                .unitPrices(List.of(new ConfirmationReservation.UnitPrice(SKU, UNIT_PRICE)))
                .build()
    }
}
