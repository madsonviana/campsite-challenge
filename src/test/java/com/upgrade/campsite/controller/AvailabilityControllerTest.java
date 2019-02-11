package com.upgrade.campsite.controller;

import com.upgrade.campsite.service.AvailabilityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@WebFluxTest(AvailabilityController.class)
public class AvailabilityControllerTest {

    @MockBean
    private AvailabilityService availabilityService;

    @Autowired
    private WebTestClient webTestClient;

    private static final List<LocalDate> dates = Stream.iterate(LocalDate.now(),
                                                                localDate -> localDate.plusDays(1))
                                                                .limit(15)
                                                                .collect(Collectors.toList());

    @Test
    public void checkAvailabilityOk() {
        when(availabilityService.checkAvailability(any(), any())).thenReturn(Flux.fromArray(dates.toArray(new LocalDate[0])));

        LocalDate initialDate = LocalDate.now();
        LocalDate finalDate = initialDate.plusDays(15);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/availabilities")
                        .queryParam("initialDate", initialDate)
                        .queryParam("finalDate", finalDate)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LocalDate.class)
                .isEqualTo(dates);

        verify(availabilityService).checkAvailability(initialDate, finalDate);
    }

    @Test
    public void checkAvailabilityOkWithoutFinalDate() {
        when(availabilityService.checkAvailability(any(), any())).thenReturn(Flux.fromArray(dates.toArray(new LocalDate[0])));

        LocalDate initialDate = LocalDate.now();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/availabilities")
                        .queryParam("initialDate", initialDate)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LocalDate.class)
                .isEqualTo(dates);

        verify(availabilityService).checkAvailability(initialDate, LocalDate.now().plusMonths(1));
    }

    @Test
    public void checkAvailabilityWithoutSendDates() {
        webTestClient.get()
                .uri("/availabilities")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(Void.class);

        verify(availabilityService, never()).checkAvailability(any(), any());
    }

}