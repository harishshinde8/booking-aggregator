package com.booking.aggregator.booking.service.impl;

import com.booking.aggregator.booking.domain.*;
import com.booking.aggregator.booking.dto.request.*;
import com.booking.aggregator.booking.repository.*;
import com.booking.aggregator.booking.service.BookingCreatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.booking.aggregator.booking.constants.BookingConstants.ALPHANUMERIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingCreatorServiceImpl implements BookingCreatorService {


    private final TripRepository tripRepo;
    private final PassengerRepository passengerRepo;
    private final BaggageRepository baggageRepo;
    private final ETicketRepository eTicketRepo;

    public Mono<Trip> createBooking(CreateBookingRequest request) {
        String pnr = generatePnr(7);

        // Build Trip object
        Trip trip = new Trip();
        trip.setId(null);
        trip.setPnr(pnr);
        trip.setCabinClass(request.getCabinClass());
        trip.setFlights(request.getFlights().stream()
                .map(f -> new Trip.Flight(
                        f.getFlightNumber(),
                        f.getDepartureAirport(),
                        f.getDepartureTimeStamp(),
                        f.getArrivalAirport(),
                        f.getArrivalTimeStamp()))
                .toList());

        // Save Trip first
        return tripRepo.save(trip)
                .flatMap(savedTrip ->
                        Flux.fromIterable(request.getPassengers())
                                .flatMap(p -> savePassengerData(p, pnr)) // handle each passenger
                                .then(Mono.just(savedTrip)) // when all passengers done
                );
    }

    private Mono<Passenger> savePassengerData(CreatePassengerRequest p, String pnr) {
        Passenger passenger = new Passenger();
        passenger.setId(null);
        passenger.setPnr(pnr);
        passenger.setPassengerNumber(p.getPassengerNumber());
        passenger.setFirstName(p.getFirstName());
        passenger.setMiddleName(p.getMiddleName());
        passenger.setLastName(p.getLastName());
        passenger.setCustomerId(p.getCustomerId());
        passenger.setSeat(p.getSeat());

        // Save passenger
        Mono<Passenger> savedPassengerMono = passengerRepo.save(passenger).cache();

        // Save all baggage items if present
        Mono<Baggage> savedBaggageMono = Mono.empty();
        if (p.getBaggageAllowance() != null) {
            savedBaggageMono = Mono.just(p.getBaggageAllowance()).flatMap(bag -> {
                Baggage b = new Baggage();
                b.setId(null);
                b.setPassengerNumber(p.getPassengerNumber());
                b.setAllowanceUnit(bag.getAllowanceUnit());
                b.setCheckedAllowanceValue(bag.getCheckedAllowanceValue());
                b.setCarryOnAllowanceValue(bag.getCarryOnAllowanceValue());
                return baggageRepo.save(b);
            });
        }

        // Save ticket if present
        Mono<ETicket> savedTicketMono = Mono.empty();
        if (p.getTicketUrl() != null) {
            ETicket t = new ETicket();
            t.setId(null);
            t.setPassengerNumber(p.getPassengerNumber());
            t.setTicketUrl(p.getTicketUrl());
            savedTicketMono = eTicketRepo.save(t);
        }

        // Combine passenger + all other saves (without blocking)
        return Mono.when(savedPassengerMono, savedBaggageMono, savedTicketMono.defaultIfEmpty(new ETicket()))
                .then(savedPassengerMono);
    }

    public static String generatePnr(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
}