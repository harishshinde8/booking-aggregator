package com.booking.aggregator.booking.service;

import com.booking.aggregator.booking.domain.Trip;
import com.booking.aggregator.booking.dto.request.CreateBookingRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.security.SecureRandom;

import static com.booking.aggregator.booking.constants.BookingConstants.ALPHANUMERIC;

@Service
public interface BookingCreatorService {

    SecureRandom RANDOM = new SecureRandom();

    Mono<Trip> createBooking(CreateBookingRequest request);

    static String generatePnr(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}
