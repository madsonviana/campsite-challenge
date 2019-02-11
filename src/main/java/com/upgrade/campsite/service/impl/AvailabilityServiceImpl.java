package com.upgrade.campsite.service.impl;

import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.repository.ReservationRepository;
import com.upgrade.campsite.service.AvailabilityService;
import com.upgrade.campsite.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

@Service
public class AvailabilityServiceImpl  implements AvailabilityService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Flux<LocalDate> checkAvailability(LocalDate initialDate, LocalDate finalDate) {
        return reservationRepository.findByRange(initialDate, finalDate)
                .collectList()
                .flatMapMany(reservations -> filterDates(reservations, initialDate, finalDate));
    }

    private Flux<LocalDate> filterDates(List<Reservation> reservations, LocalDate initialDate, LocalDate finalDate) {
        List<LocalDate> days = DateUtil.getDaysFromRange(initialDate, finalDate);
        Iterator<LocalDate> daysIterator = days.iterator();
        while(daysIterator.hasNext()) {
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
    }

}
