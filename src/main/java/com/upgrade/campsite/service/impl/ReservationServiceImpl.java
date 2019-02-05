package com.upgrade.campsite.service.impl;

import com.upgrade.campsite.exception.BusinessException;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.repository.ReservationRepository;
import com.upgrade.campsite.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Flux<Date> checkAvailability(Date arrivalDate, Date departureDate) {
        return null;
    }

    @Override
    public Mono<Reservation> book(Reservation reservation) throws BusinessException {
        throw new BusinessException("Ocorreu um erro ao adicionar a reserva");
        //return reservationRepository.save(reservation);
    }

    @Override
    public Mono<Reservation> findReservationById(String id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Mono<Void> cancel(String id) throws BusinessException {
        return null;
    }

    @Override
    public Mono<Reservation> update(String id, Date arrivalDate, Date departureDate) throws BusinessException {
        return null;
    }
}
