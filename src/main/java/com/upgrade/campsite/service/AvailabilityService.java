package com.upgrade.campsite.service;

import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface AvailabilityService {

    Flux<LocalDate> checkAvailability(LocalDate arrivalDate, LocalDate departureDate);

}
