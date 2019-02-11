package com.upgrade.campsite.util;

import com.upgrade.campsite.exception.*;
import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.repository.ReservationRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ValidationUtil {

    private static final int MAX_DAYS = 3;

    @Autowired
    private ReservationRepository reservationRepository;

    public Mono<Reservation> validateOverlapping(Reservation r) {
        Long countOverlapping;
        if(StringUtils.isNotBlank(r.getId())) {
            countOverlapping = reservationRepository.countOverlappingReservations(r.getArrivalDate(),r.getDepartureDate(), r.getId()).block();
        } else {
            countOverlapping = reservationRepository.countOverlappingReservations(r.getArrivalDate(),r.getDepartureDate()).block();
        }
        return (countOverlapping > 0) ? Mono.error(OverlappingError::new) : Mono.just(r);
    }

    public Mono<Reservation> validateMaxDays(Reservation r) {
        return (ChronoUnit.DAYS.between(r.getArrivalDate(), r.getDepartureDate()) > MAX_DAYS ? Mono.error(MaxReservationDaysExcedeedError::new) : Mono.just(r) );
    }

    public Mono<Reservation> validateReservationDate(Reservation r) {

        // Check arrival date in the past
        if(r.getArrivalDate().isBefore(LocalDate.now())) {
            return Mono.error(PastArrivalDateError::new);
        }

        // Check arrival date is today
        if(r.getArrivalDate().isEqual(LocalDate.now())) {
            return Mono.error(TodayArrivalDateError::new);
        }

        // Check arrival date before next month
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        if(r.getArrivalDate().isAfter(nextMonth)) {
            return Mono.error(FarFutureArrivalDateError::new);
        }

        return Mono.just(r);
    }

    public Mono<Reservation> validateRequired(Reservation r) {
        if(r.getArrivalDate() ==null
                || r.getDepartureDate() == null
                || StringUtils.isBlank(r.getEmail())
                || StringUtils.isBlank(r.getFullName())) {
            return Mono.error(RequiredFieldsError::new);
        }
        return Mono.just(r);
    }

    public Mono<Reservation> validateCancelDepartureDate(Reservation r) {
        if(r.getDepartureDate().isBefore(LocalDate.now())) {
            return Mono.error(PastDepartureDateError::new);
        }

        return Mono.just(r);
    }
}