package com.upgrade.campsite.controller;

import com.upgrade.campsite.exception.BusinessException;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.service.ReservationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import java.util.Date;

@RunWith(SpringRunner.class)
@WebFluxTest
public class ReservationControllerTest {

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private WebTestClient webTestClient;

    private final Reservation reservation = Reservation.builder()
            .arrivalDate(new Date())
            .departureDate(new Date())
            .fullName("Madson Viana")
            .email("me@madsonviana.info")
            .build();

    private final Reservation reservationCreated = Reservation.builder()
            .id("5c5a198b3acf52388d17c8e1")
            .arrivalDate(new Date())
            .departureDate(new Date())
            .fullName("Madson Viana")
            .email("me@madsonviana.info")
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
        when(reservationService.findById("1")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/reservations/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }

    @Test
    public void createReservationOK() throws Exception {
        when(reservationService.book(reservation)).thenReturn(Mono.just(reservationCreated));

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reservation.class)
                .isEqualTo(reservationCreated);

    }


    @Test
    public void createReservationError() throws Exception {
        doThrow(new BusinessException()).when(reservationService).book(reservation);

        webTestClient.post()
                .uri("/api/reservations")
                .body(BodyInserters.fromObject(reservation))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(Void.class);

    }

}