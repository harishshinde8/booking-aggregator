package com.booking.aggregator.booking.repository;

import com.booking.aggregator.booking.domain.Baggage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BaggageRepository extends ReactiveMongoRepository<Baggage, String> {
    Flux<Baggage> findByPassengerNumber(Integer passengerNumber);
}
