package com.booking.aggregator.booking.repository;

import com.booking.aggregator.booking.domain.Baggage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BaggageRepository extends ReactiveMongoRepository<Baggage, String> {
    Mono<Baggage> findByPnrAndPassengerNumber(String pnr, Integer passengerNumber);
    Mono<Baggage> findByPassengerNumber(Integer passengerNumber);
}
