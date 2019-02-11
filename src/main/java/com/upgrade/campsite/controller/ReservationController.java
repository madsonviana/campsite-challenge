package com.upgrade.campsite.controller;

import com.upgrade.campsite.dto.DatesDTO;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.service.ReservationService;
import com.upgrade.campsite.util.QueueUtil;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.Serializable;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @GetMapping("/{id}")
    public Mono<Reservation> findById(@PathVariable String id) {
        return reservationService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Reservation> createReservation(@RequestBody Reservation reservation) throws JMSException {
        Message result = jmsTemplate.sendAndReceive(QueueUtil.BOOKING_QUEUE,
                session -> QueueUtil.createObjectMessage(session, reservation));

        Serializable response = ((ActiveMQObjectMessage) result).getObject();
        if (response instanceof Throwable) {
            return Mono.error((Throwable) response);
        }
        return Mono.just((Reservation) response);
    }

    @DeleteMapping("/{id}")
    public Mono<Reservation> cancel(@PathVariable String id) {
        return reservationService.cancel(id);
    }

    @PutMapping("/{id}")
    public Mono<Reservation> update(@PathVariable String id, @RequestBody DatesDTO dto) {
        return reservationService.update(id, dto.getArrivalDate(), dto.getDepartureDate());
    }
}
