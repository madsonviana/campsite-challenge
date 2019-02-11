package com.upgrade.campsite;

import com.upgrade.campsite.controller.ReservationController;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.repository.ReservationRepository;
import com.upgrade.campsite.service.ReservationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@WebFluxTest
public class MultipleRequestTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation reservation;

    @Autowired
    private WebTestClient webTestClient;

    @Before
    public void setup() {
        reservation = Reservation.builder()
                .fullName("Madson Viana")
                .email("me@madsonviana.info")
                .arrivalDate(LocalDate.now().plusDays(1))
                .departureDate(LocalDate.now().plusMonths(2))
                .build();
    }

    @Test
    public void multipleRequestTest() {
        Stream
                .iterate(reservation, r -> r)
                .limit(1000)
                .parallel()
                .forEach(r ->
                    webTestClient.post()
                            .uri("/api/reservations")
                            .body(BodyInserters.fromObject(r))
                );
    }

}
