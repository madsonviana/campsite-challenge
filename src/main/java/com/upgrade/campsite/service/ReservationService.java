package com.upgrade.campsite.service;

import com.upgrade.campsite.exception.BusinessException;
import com.upgrade.campsite.model.Reservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface ReservationService {

    Flux<Date> checkAvailability(Date arrivalDate, Date departureDate);

    Mono<Reservation> book(Reservation reservation) throws BusinessException;

    Mono<Reservation> findReservationById(String id);

    Mono<Void> cancel(String id) throws BusinessException;

    Mono<Reservation> update(String id, Date arrivalDate, Date departureDate) throws BusinessException;

}
