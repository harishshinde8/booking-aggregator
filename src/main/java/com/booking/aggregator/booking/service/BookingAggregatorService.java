package com.booking.aggregator.booking.service;

import com.booking.aggregator.booking.dto.response.TripResponseDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface BookingAggregatorService {

     Mono<TripResponseDTO> getBooking(String pnr);
}
