package com.booking.aggregator.booking.service;

import com.booking.aggregator.booking.domain.Trip;
import com.booking.aggregator.booking.dto.request.CreateBookingRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.security.SecureRandom;

@Service
public interface BookingCreatorService {

    SecureRandom RANDOM = new SecureRandom();

    Mono<Trip> createBooking(CreateBookingRequest request);
}
