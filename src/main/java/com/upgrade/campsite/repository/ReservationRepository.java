package com.upgrade.campsite.repository;

import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.model.Status;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {

    Mono<Reservation> findByIdAndStatus(String id, Status status);

//    @Query("select count(r) from Reservation where (?1 between r.arrivalDate and r.departureDate) " +
//            "or (?2 between r.arrivalDate and r.departureDate) " +
//            "or (r.arrivalDate between ?1 and ?2)" +
//            "or (r.departure between ?1 and ?2)")
//    @Query(value = "{ $or : [" +
//                "{ ?0 : {$gt : 'arrivalDate', $lt : 'departureDate'} }, " +
//                "{ ?1 : {$gt : 'arrivalDate', $lt : 'departureDate'} }, " +
//                "{ 'arrivalDate' : {$gt : ?0, $lt : ?1} }, " +
//                "{ 'departureDate' : {$gt : ?0, $lt : ?1}} " +
//            " ]}", count = true)
    Mono<Long> countByArrivalDateBetween(Date arrivalDate, Date departureDate);
}
