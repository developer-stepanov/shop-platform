package com.stepanov

import com.stepanov.enums.Currency
import com.stepanov.enums.OrderStatus
import com.stepanov.exceptions.EmptyStripeSession
import com.stepanov.kafka.events.topics.payments.CheckoutPaymentLink
import com.stepanov.kafka.events.topics.stock.ConfirmationReservation
import com.stepanov.kafka.events.topics.stock.PaymentDetails
import com.stepanov.messaging.listener.PaymentListener
import com.stepanov.repository.PaymentRepository
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils

import static com.stepanov.kafka.topics.KafkaTopics.ORDER_PREPARE_PAYMENT_TOPIC
import static com.stepanov.kafka.topics.KafkaTopics.PAYMENT_NOTIFICATION_TOPIC

@EmbeddedKafka(kraft = true, partitions = 1, topics = [ORDER_PREPARE_PAYMENT_TOPIC,
                                                        PAYMENT_NOTIFICATION_TOPIC])
//exception triggered because property not set up
@SpringBootTest(properties = "stripe.payment-confirmation.url=")
class ExceptionPaymentListenerIntegrationSpec extends AbstractIntegrationTests {

    static final String ORDER_ID = '7f000001-976a-1676-8197-6ac852ab0001'

    @Autowired
    PaymentListener paymentListener

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    ConsumerFactory<String, CheckoutPaymentLink> consumerFactory

    Consumer<String, CheckoutPaymentLink> consumer

    void setup() {
        consumer = consumerFactory.createConsumer()
        consumer.assign([new TopicPartition(PAYMENT_NOTIFICATION_TOPIC, 0)])
    }

    void 'should not save Payment and throw EmptyStripeSession on failed Stripe session'() {
        given: 'a valid ConfirmationReservation event'
            var evt = validEvent()
        when: 'the payment listener processes the event'
            paymentListener.on(evt, evt.orderId().toString())
        and: 'we attempt to consume from the PAYMENT_NOTIFICATION_TOPIC'
            var checkoutPaymentLinkConsumerRecord =
                    KafkaTestUtils.getSingleRecord(consumer, PAYMENT_NOTIFICATION_TOPIC)
        then: 'an EmptyStripeSession is thrown with the expected message'
            var ex = thrown EmptyStripeSession
            ex.message == "OrderId: ${ORDER_ID}"
        and: 'no Payment entity has been persisted'
            paymentRepository.count() == 0
        and: 'no event sent to the PAYMENT_NOTIFICATION_TOPIC'
            checkoutPaymentLinkConsumerRecord == null
    }

    private static ConfirmationReservation validEvent() {
        ConfirmationReservation.builder()
                .orderId(UUID.fromString(ORDER_ID))
                .orderStatus(OrderStatus.RESERVED)
                .paymentDetails(
                    PaymentDetails.builder()
                            .totalPayment(BigDecimal.valueOf(1000))
                            .currency(Currency.EUR).build()).build()
    }
}
