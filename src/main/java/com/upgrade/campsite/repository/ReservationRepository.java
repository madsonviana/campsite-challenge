package com.upgrade.campsite.repository;

import com.upgrade.campsite.model.Reservation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {
}
