package com.upgrade.campsite.service.impl;

import com.upgrade.campsite.exception.BusinessException;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.model.Status;
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
        return Mono.just(reservation)
                .flatMap(this::checkOverlapping)
                .map(r -> { r.setStatus(Status.PROCESSING); return r;})
                .flatMap(reservationRepository::save);
    }

    @Override
    public Mono<Reservation> findById(String id) {
        return reservationRepository.findById(id);
    }

    @Override
    public Mono<Reservation> cancel(String id) throws BusinessException {
        return reservationRepository.findById(id)
                .map(r -> Reservation.builder()
                            .id(r.getId())
                            .fullName(r.getFullName())
                            .departureDate(r.getDepartureDate())
                            .arrivalDate(r.getArrivalDate())
                            .email(r.getEmail())
                            .status(Status.CANCELED)
                            .build())
                .flatMap(reservationRepository::save);
    }

    @Override
    public Mono<Reservation> update(String id, Date arrivalDate, Date departureDate) throws BusinessException {
        // Todo validar reserva

        return reservationRepository.findByIdAndStatus(id, Status.PROCESSING)
                .map(r -> Reservation.builder()
                            .id(r.getId())
                            .fullName(r.getFullName())
                            .departureDate(departureDate)
                            .arrivalDate(arrivalDate)
                            .email(r.getEmail())
                            .status(r.getStatus())
                            .build())
                .flatMap(reservationRepository::save);
    }

    private Mono<Reservation> checkOverlapping(Reservation r) {
        return reservationRepository.countByArrivalDateBetween(r.getArrivalDate(),r.getDepartureDate())
                .flatMap(c -> (c > 0) ? Mono.error(new BusinessException()) : Mono.just(r));

    }
}
