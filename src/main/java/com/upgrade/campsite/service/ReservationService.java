package com.upgrade.campsite.service;

import com.upgrade.campsite.model.Reservation;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReservationService {

    Flux<LocalDate> checkAvailability(LocalDate arrivalDate, LocalDate departureDate);

    Message<Reservation> book(Reservation reservation);

    Mono<Reservation> findById(String id);

    Mono<Reservation> cancel(String id);

    Mono<Reservation> update(String id, LocalDate arrivalDate, LocalDate departureDate);

}
