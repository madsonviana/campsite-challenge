package com.upgrade.campsite.service.impl;

import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.repository.ReservationRepository;
import com.upgrade.campsite.service.ReservationService;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.ObjectMessage;
import java.time.LocalDate;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReservationServiceImplTest {

    @Autowired
    private ReservationRepository repository;

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
    public void book() {
        final Results results = new Results();

        Stream
                .iterate(getNewReservation(), r -> getNewReservation())
                .limit(2)
                .parallel()
                .forEach(r ->
                        jmsTemplate.sendAndReceive("bookQueue", session -> {
                            ObjectMessage message = session.createObjectMessage();
                            message.setObject(r);
                            return message;
                        }));

        repository.findAll().subscribe(System.out::println);
        Assert.assertEquals(1, results.getSuccess());
        Assert.assertEquals(999, results.getError());
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