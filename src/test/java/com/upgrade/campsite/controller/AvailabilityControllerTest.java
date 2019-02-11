package com.upgrade.campsite.controller;

import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.service.AvailabilityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ReservationController.class)
public class AvailabilityControllerTest {

    @MockBean
    private AvailabilityService availabilityService;

    @Autowired
    private WebTestClient webTestClient;

    private static final Flux<LocalDate> dates = Flux.fromStream(
                                                    Stream.iterate(
                                                                LocalDate.now(),
                                                                localDate -> localDate.plusDays(1))
                                                            .limit(15));


    @Test
    public void checkAvailabilityOk() {
        when(availabilityService.checkAvailability(any(), any())).thenReturn(dates);

        webTestClient.get()
                .uri("/availabilities")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Flux.class)
                .isEqualTo(dates);
    }

}