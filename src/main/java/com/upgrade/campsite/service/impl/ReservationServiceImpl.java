package com.upgrade.campsite.service.impl;

import com.upgrade.campsite.exception.ReservationNotFound;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.model.Status;
import com.upgrade.campsite.repository.ReservationRepository;
import com.upgrade.campsite.service.ReservationService;
import com.upgrade.campsite.util.QueueUtil;
import com.upgrade.campsite.util.ValidationUtil;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    @JmsListener(destination = QueueUtil.BOOKING_QUEUE)
    public void book(@Payload Reservation reservation,
                     @Header(JmsHeaders.REPLY_TO) ActiveMQTempQueue replyTo) {
        Mono.just(reservation)
            .flatMap(this::applyValidations)
            .map(r -> { r.setStatus(Status.ACTIVE); return r;})
            .flatMap(reservationRepository::save)
            .subscribe(reservationCreated -> jmsTemplate.convertAndSend(replyTo, reservationCreated),
                       throwable -> jmsTemplate.convertAndSend(replyTo, throwable));
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
