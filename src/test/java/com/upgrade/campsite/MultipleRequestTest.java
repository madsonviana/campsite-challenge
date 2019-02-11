package com.upgrade.campsite;

import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.util.QueueUtil;
import lombok.Data;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.stream.Stream;

/**
 * Tests of concurrency booking
 *
 * @author Madson Viana
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MultipleRequestTest {

    private static final int BOOKING_QUANTITY = 1000;

    @Autowired
    private JmsTemplate jmsTemplate;

    public Reservation getNewReservation() {
        return Reservation.builder()
                .fullName("Madson Viana")
                .email("me@madsonviana.info")
                .arrivalDate(LocalDate.now().plusDays(1))
                .departureDate(LocalDate.now().plusDays(2))
                .build();
    }

    @Test
    public void multiplBooking() {
        final Results results = new Results();

        Stream
                .iterate(getNewReservation(), r -> getNewReservation())
                .limit(BOOKING_QUANTITY)
                .parallel()
                .forEach(r -> {
                    Message message = jmsTemplate.sendAndReceive(QueueUtil.BOOKING_QUEUE,
                            session -> QueueUtil.createObjectMessage(session, r));
                    Serializable response = null;
                    try {
                        response = ((ActiveMQObjectMessage) message).getObject();
                    } catch (JMSException e) { throw new RuntimeException(e); }

                    if (response instanceof Throwable) {
                        results.addError();
                    } else {
                        results.addSuccess();
                    }
                });

        Assert.assertEquals(1, results.getSuccess());
        Assert.assertEquals(BOOKING_QUANTITY - 1, results.getError());
    }

    @Data
    private class Results {
        private int success = 0;
        private int error = 0;

        synchronized void addSuccess() {
            success++;
        }

        synchronized void addError() {
            error++;
        }
    }

}
