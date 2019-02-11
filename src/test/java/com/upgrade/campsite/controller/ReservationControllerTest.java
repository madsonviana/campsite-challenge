package com.upgrade.campsite.controller;

import com.upgrade.campsite.exception.*;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.model.Status;
import com.upgrade.campsite.service.ReservationService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ReservationController.class)
public class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private JmsTemplate jmsTemplate;

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private ActiveMQObjectMessage reservationCreatedMessage;

    private final Reservation reservation = Reservation.builder()
            .arrivalDate(LocalDate.now().plusDays(1))
            .departureDate(LocalDate.now().plusDays(2))
            .fullName("Madson Viana")
            .email("me@madsonviana.info")
            .build();

    private final Reservation reservationCreated = Reservation.builder()
            .id("5c5a198b3acf52388d17c8e1")
            .arrivalDate(LocalDate.now().plusDays(1))
            .departureDate(LocalDate.now().plusDays(2))
            .fullName("Madson Viana")
            .email("me@madsonviana.info")
            .status(Status.ACTIVE)
            .build();

    private final Reservation reservationCanceled = Reservation.builder()
            .id("5c5a198b3acf52388d17c8e1")
            .arrivalDate(LocalDate.now().plusDays(1))
            .departureDate(LocalDate.now().plusDays(2))
            .fullName("Madson Viana")
            .email("me@madsonviana.info")
            .status(Status.CANCELED)
            .build();

    @Test
    public void findByIdOK() {
        when(reservationService.findById("1")).thenReturn(Mono.just(reservation));

        webTestClient.get()
                .uri("/api/reservations/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reservation.class)
                .isEqualTo(reservation);
    }

    @Test
    public void findByIdNotFound() {
        when(reservationService.findById("1")).thenReturn(Mono.error(ReservationNotFound::new));

        webTestClient.get()
                .uri("/api/reservations/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }

    // CREATE
    @Test
    public void createReservationOK() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(reservationCreated);
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Reservation.class)
                .isEqualTo(reservationCreated);
    }

    @Test
    public void createReservationFarFurureArrivalDateError() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(new FarFutureArrivalDateError());
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void createReservationMaxReservationDaysExcedeedError() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(new MaxReservationDaysExcedeedError());
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void createReservationOverlappingError() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(new OverlappingError());
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void createReservationPastArrivalDateError() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(new PastArrivalDateError());
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void createReservationRequiredFieldsError() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(new RequiredFieldsError());
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void createReservationTodayArrivalDateError() throws Exception {
        when(reservationCreatedMessage.getObject()).thenReturn(new TodayArrivalDateError());
        when(jmsTemplate.sendAndReceive(any(String.class), any())).thenReturn(reservationCreatedMessage);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    // UPDATE
    @Test
    public void updateReservationOK() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.just(reservationCreated));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reservation.class)
                .isEqualTo(reservationCreated);
    }

    @Test
    public void updateReservationFarFurureArrivalDateError() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(FarFutureArrivalDateError::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void updateReservationMaxReservationDaysExcedeedError() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(MaxReservationDaysExcedeedError::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void updateReservationOverlappingError() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(OverlappingError::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void updateReservationPastArrivalDateError() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(PastArrivalDateError::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void updateReservationRequiredFieldsError() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(RequiredFieldsError::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void updateReservationTodayArrivalDateError() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(TodayArrivalDateError::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);
    }

    @Test
    public void updateReservationNotFound() {
        when(reservationService.update(any(), any(), any())).thenReturn(Mono.error(ReservationNotFound::new));

        webTestClient.put()
                .uri("/api/reservations/1")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }


    // CANCEL
    @Test
    public void cancelReservationOK() {
        when(reservationService.cancel(any())).thenReturn(Mono.just(reservationCanceled));

        webTestClient.delete()
                .uri("/api/reservations/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reservation.class)
                .isEqualTo(reservationCanceled);
    }

    @Test
    public void cancelReservationNotFound() {
        when(reservationService.cancel(any())).thenReturn(Mono.error(ReservationNotFound::new));

        webTestClient.delete()
                .uri("/api/reservations/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }

    @Test
    public void cancelReservationPastDepartureDateError() {
        when(reservationService.cancel(any())).thenReturn(Mono.error(PastDepartureDateError::new));

        webTestClient.delete()
                .uri("/api/reservations/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }
}