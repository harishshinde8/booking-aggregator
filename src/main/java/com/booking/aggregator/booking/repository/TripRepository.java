package com.booking.aggregator.booking.repository;

import com.booking.aggregator.booking.domain.Trip;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TripRepository extends ReactiveMongoRepository<Trip, String> {
    Mono<Trip> findByPnr(String pnr);
}