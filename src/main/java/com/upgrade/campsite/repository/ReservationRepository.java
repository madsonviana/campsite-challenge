package com.upgrade.campsite.repository;

import com.upgrade.campsite.model.Reservation;
import com.upgrade.campsite.model.Status;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {

    Mono<Reservation> findByIdAndStatus(String id, Status status);

    @Query(value = "{ $and : [ " +
                        "{ '$or' : [ " +
                            "{ 'departureDate' : { $gt : ?0, $lt: ?1 }}, " +
                            "{ 'arrivalDate'   : { $gt : ?0, $lt: ?1 }}, " +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $lt : ?0 }}, " +
                                "{'departureDate' : { $gt : ?0 }} " +
                            "]}, " +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $lt : ?1 }}, " +
                                "{'departureDate' : { $gt : ?1 }} " +
                            "]}," +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $eq : ?0 }}, " +
                                "{'departureDate' : { $eq : ?1 }} " +
                            "]}]}," +
                        "{ 'status' : {$eq : 'ACTIVE' }} " +
                    "] }")
    Flux<Reservation> findByRange(LocalDate arrivalDate, LocalDate departureDate);

    @Query(value = "{ $and : [ " +
                        "{ '$or' : [ " +
                            "{ 'departureDate' : { $gt : ?0, $lt: ?1 }}, " +
                            "{ 'arrivalDate'   : { $gt : ?0, $lt: ?1 }}, " +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $lt : ?0 }}, " +
                                "{'departureDate' : { $gt : ?0 }} " +
                            "]}, " +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $lt : ?1 }}, " +
                                "{'departureDate' : { $gt : ?1 }} " +
                            "]}," +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $eq : ?0 }}, " +
                                "{'departureDate' : { $eq : ?1 }} " +
                            "]}]}," +
                        "{ 'status' : {$eq : 'ACTIVE' }} " +
                    "] }", count = true)
    Mono<Long> countOverlappingReservations(LocalDate arrivalDate, LocalDate departureDate);

    @Query(value = "{ $and : [ " +
                        "{ '$or' : [ " +
                            "{ 'departureDate' : { $gt : ?0, $lt: ?1 }}, " +
                            "{ 'arrivalDate'   : { $gt : ?0, $lt: ?1 }}, " +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $lt : ?0 }}, " +
                                "{'departureDate' : { $gt : ?0 }} " +
                            "]}, " +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $lt : ?1 }}, " +
                                "{'departureDate' : { $gt : ?1 }} " +
                            "]}," +
                            "{ $and            : [ " +
                                "{'arrivalDate'   : { $eq : ?0 }}, " +
                                "{'departureDate' : { $eq : ?1 }} " +
                            "]}]}," +
                        "{ 'id' : {$ne : ?2 }}, " +
                        "{ 'status' : {$eq : 'ACTIVE' }} " +
                    "] }", count = true)
    Mono<Long> countOverlappingReservations(LocalDate arrivalDate, LocalDate departureDate, String id);
}
