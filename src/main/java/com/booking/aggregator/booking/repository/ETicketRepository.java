package com.booking.aggregator.booking.repository;

import com.booking.aggregator.booking.domain.ETicket;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ETicketRepository extends ReactiveMongoRepository<ETicket, String> {
    Mono<ETicket> findByPnrAndPassengerNumber(String pnr, int passengerNumber);
}