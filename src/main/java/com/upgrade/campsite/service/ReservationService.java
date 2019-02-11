package com.upgrade.campsite.service;

import com.upgrade.campsite.model.Reservation;
import org.apache.activemq.command.ActiveMQTempQueue;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReservationService {

    void book(Reservation reservation, ActiveMQTempQueue replyTo);

    Mono<Reservation> findById(String id);

    Mono<Reservation> cancel(String id);

    Mono<Reservation> update(String id, LocalDate arrivalDate, LocalDate departureDate);

}
