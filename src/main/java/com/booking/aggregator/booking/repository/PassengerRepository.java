package com.booking.aggregator.booking.repository;

import com.booking.aggregator.booking.domain.Passenger;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {
    Flux<Passenger> findByPnr(String pnr);
}
