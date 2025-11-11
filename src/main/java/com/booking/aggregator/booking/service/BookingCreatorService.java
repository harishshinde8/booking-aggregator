package com.booking.aggregator.booking.service;

import com.booking.aggregator.booking.domain.*;
import com.booking.aggregator.booking.dto.request.*;
import com.booking.aggregator.booking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.security.SecureRandom;

import static com.booking.aggregator.booking.constants.BookingConstants.ALPHANUMERIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCreatorService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final TripRepository tripRepo;
    private final PassengerRepository passengerRepo;
    private final BaggageRepository baggageRepo;
    private final ETicketRepository eTicketRepo;

    public Mono<Trip> createBooking(CreateBookingRequest request) {
        String pnr = generatePnr(7);
        Trip trip = new Trip();
        trip.setPnr(pnr);
        trip.setCabinClass(request.getCabinClass());
        trip.setFlights(request.getFlights().stream().map(f ->
                        new Trip.Flight(f.getFlightNumber(), f.getDepartureAirport(),
                                f.getDepartureTimeStamp(), f.getArrivalAirport(),
                                f.getArrivalTimeStamp()))
                .toList());

        Mono<Trip> savedTrip = tripRepo.save(trip);

        Flux<Passenger> passengers = Flux.fromIterable(request.getPassengers())
                .flatMap(p -> {
                    Passenger passenger = new Passenger();
                    passenger.setPnr(request.getPnr());
                    passenger.setPassengerNumber(p.getPassengerNumber());
                    passenger.setFirstName(p.getFirstName());
                    passenger.setMiddleName(p.getMiddleName());
                    passenger.setLastName(p.getLastName());
                    passenger.setCustomerId(p.getCustomerId());
                    passenger.setSeat(p.getSeat());

                    Mono<Passenger> savedPassenger = passengerRepo.save(passenger);

                    Mono<Baggage> savedBaggage = Mono.empty();
                    if (p.getBaggageAllowance() != null) {
                        Baggage b = new Baggage();
                        b.setPnr(request.getPnr());
                        b.setPassengerNumber(p.getPassengerNumber());
                        b.setAllowanceUnit(p.getBaggageAllowance().getAllowanceUnit());
                        b.setCheckedAllowanceValue(p.getBaggageAllowance().getCheckedAllowanceValue());
                        b.setCarryOnAllowanceValue(p.getBaggageAllowance().getCarryOnAllowanceValue());
                        savedBaggage = baggageRepo.save(b);
                    }

                    Mono<ETicket> savedTicket = Mono.empty();
                    if (p.getTicketUrl() != null) {
                        ETicket t = new ETicket();
                        t.setPnr(request.getPnr());
                        t.setPassengerNumber(p.getPassengerNumber());
                        t.setTicketUrl(p.getTicketUrl());
                        savedTicket = eTicketRepo.save(t);
                    }

                    return Mono.when(savedPassenger, savedBaggage, savedTicket)
                            .thenReturn(passenger);
                });

        return savedTrip.flatMap(trip1 -> passengers.collectList().thenReturn(trip1));
    }

    public static String generatePnr(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

}