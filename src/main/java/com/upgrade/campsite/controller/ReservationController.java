package com.upgrade.campsite.controller;

import com.upgrade.campsite.exception.BusinessException;
import com.upgrade.campsite.exception.ReservationNotFound;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


    @GetMapping("/api/reservations/{id}")
    public Mono<Reservation> findById(@PathVariable String id) {
        return reservationService.findReservationById(id)
                .switchIfEmpty(Mono.error(ReservationNotFound::new));
    }

    @PostMapping("/api/reservations")
    public Mono<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            return reservationService.book(reservation);
        } catch (BusinessException e) {
            return Mono.error(e);
        }
    }
}
