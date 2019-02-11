package com.upgrade.campsite.service.impl;

import com.upgrade.campsite.exception.ReservationNotFound;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.model.Status;
import com.upgrade.campsite.repository.ReservationRepository;
import com.upgrade.campsite.service.ReservationService;
import com.upgrade.campsite.util.DateUtil;
import com.upgrade.campsite.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public Flux<LocalDate> checkAvailability(LocalDate initialDate, LocalDate finalDate) {
        return reservationRepository.findByRange(initialDate, finalDate)
                .collectList()
                .flatMapMany(reservations -> {
                    List<LocalDate> days = DateUtil.getDaysFromRange(initialDate, finalDate);
                    Iterator<LocalDate> daysIterator = days.iterator();
                    for( ; daysIterator.hasNext() ;  ) {
                        LocalDate date = daysIterator.next();
                        for(Reservation reservation : reservations) {
                            if(date.isEqual(reservation.getArrivalDate())
                                    || (date.isAfter(reservation.getArrivalDate())
                                    && date.isBefore(reservation.getDepartureDate()))) {
                                daysIterator.remove();
                            }
                        }
                    }
                     return Flux.fromArray(days.toArray(new LocalDate[0]));
                });
    }

    @Override
    @JmsListener(destination = "bookQueue")
    public Message<Reservation> book(Reservation reservation) {
        return MessageBuilder.withPayload(Mono.just(reservation)
                .flatMap(this::applyValidations)
                .map(r -> { r.setStatus(Status.ACTIVE); return r;})
                .flatMap(reservationRepository::save)
                .block())
                .build();
    }

    @Override
    public Mono<Reservation> findById(String id) {
        return reservationRepository.findById(id)
                .switchIfEmpty(Mono.error(ReservationNotFound::new));
    }

    @Override
    public Mono<Reservation> cancel(String id) {
        return reservationRepository.findByIdAndStatus(id, Status.ACTIVE)
                .switchIfEmpty(Mono.error(ReservationNotFound::new))
                .flatMap(validationUtil::validateCancelDepartureDate)
                .map(r -> { r.setStatus(Status.CANCELED); return r;})
                .flatMap(reservationRepository::save);
    }

    @Override
    public Mono<Reservation> update(String id, LocalDate arrivalDate, LocalDate departureDate) {
        return reservationRepository.findByIdAndStatus(id, Status.ACTIVE)
                .switchIfEmpty(Mono.error(ReservationNotFound::new))
                .map(r -> {
                    r.setArrivalDate(arrivalDate);
                    r.setDepartureDate(departureDate);
                    return r;
                })
                .flatMap(this::applyValidations)
                .flatMap(reservationRepository::save);
    }

    private Mono<Reservation> applyValidations(Reservation reservation) {
        return Mono.just(reservation)
                .flatMap(validationUtil::validateRequired)
                .flatMap(validationUtil::validateReservationDate)
                .flatMap(validationUtil::validateMaxDays)
                .flatMap(validationUtil::validateOverlapping);
    }

}
