package com.booking.aggregator.booking.service;

import com.booking.aggregator.booking.domain.*;
import com.booking.aggregator.booking.dto.response.*;
import com.booking.aggregator.booking.exception.NotFoundException;
import com.booking.aggregator.booking.repository.*;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingAggregatorService {

    private final TripRepository tripRepo;
    private final PassengerRepository passengerRepo;
    private final BaggageRepository baggageRepo;
    private final ETicketRepository eTicketRepo;
    private final Vertx vertx;

    /**
     * Fetch complete booking info for a PNR
     */
    public Mono<TripResponseDTO> getBooking(String pnr) {
        log.info("BookingAggregatorService | Fetching booking details for PNR: {}", pnr);

        return tripRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new NotFoundException("PNR not found: " + pnr)))
                .flatMap(trip -> mapPassengers(trip)
                        .collectList()
                        .map(passengers -> buildTripResponse(trip, passengers))
                )
                .doOnSuccess(response -> {
                    log.info("BookingAggregatorService | Successfully fetched booking for PNR: {}", response.getPnr());
                    publishPnrEvent(response.getPnr());
                })
                .doOnError(error -> log.error("BookingAggregatorService | Error fetching booking for PNR {}: {}", pnr, error.getMessage()))
                .onErrorResume(ex -> {
                    // If not found, propagate as-is; for others, wrap it in a RuntimeException
                    if (ex instanceof NotFoundException) {
                        return Mono.error(ex);
                    } else {
                        log.error("BookingAggregatorService | Unexpected error for PNR {}: {}", pnr, ex.getMessage(), ex);
                        return Mono.error(new RuntimeException("Internal server error while fetching booking data"));
                    }
                });
    }

    /**
     * Fetch passengers for the trip and map them to PassengerResponseDTO
     */
    private Flux<PassengerResponseDTO> mapPassengers(Trip trip) {
        return passengerRepo.findByPnr(trip.getPnr())
                .flatMap(passenger -> Mono.zip(
                        fetchBaggage(passenger),
                        fetchETicket(passenger)
                ).map(tuple -> mapPassengerToDto(passenger, tuple.getT1(), tuple.getT2())));
    }

    /**
     * Convert Passenger + Baggage + Ticket -> PassengerResponseDTO
     */
    private PassengerResponseDTO mapPassengerToDto(Passenger passenger, Baggage baggage, ETicket ticket) {
        return PassengerResponseDTO.builder()
                .passengerNumber(passenger.getPassengerNumber())
                .customerId(passenger.getCustomerId())
                .fullName(Stream.of(passenger.getFirstName(), passenger.getMiddleName(), passenger.getLastName())
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(" ")))
                .seat(passenger.getSeat())
                .ticketUrl(ticket != null ? ticket.getTicketUrl() : null)
                .baggageAllowance(baggage != null ? BaggageAllowanceResponseDTO.builder()
                        .passengerNumber(baggage.getPassengerNumber())
                        .allowanceUnit(baggage.getAllowanceUnit())
                        .checkedAllowanceValue(baggage.getCheckedAllowanceValue())
                        .carryOnAllowanceValue(baggage.getCarryOnAllowanceValue())
                        .build() : null)
                .build();
    }

    /**
     * Fetch baggage allowance for a passenger
     */
    private Mono<Baggage> fetchBaggage(Passenger passenger) {
        return baggageRepo.findByPnrAndPassengerNumber(passenger.getPnr(), passenger.getPassengerNumber())
                .switchIfEmpty(Mono.empty())
                .doOnError(err -> log.warn("BookingAggregatorService | Failed to fetch baggage for passenger {}: {}", passenger.getPassengerNumber(), err.getMessage()));
    }

    /**
     * Fetch e-ticket for a passenger
     */
    private Mono<ETicket> fetchETicket(Passenger passenger) {
        return eTicketRepo.findByPnrAndPassengerNumber(passenger.getPnr(), passenger.getPassengerNumber())
                .switchIfEmpty(Mono.empty())
                .doOnError(err -> log.warn("BookingAggregatorService | Failed to fetch ticket for passenger {}: {}", passenger.getPassengerNumber(), err.getMessage()));
    }

    /**
     * Build TripResponseDTO
     */
    private TripResponseDTO buildTripResponse(Trip trip, List<PassengerResponseDTO> passengers) {
        List<FlightResponseDTO> flights = trip.getFlights().stream()
                .map(f -> FlightResponseDTO.builder()
                        .flightNumber(f.getFlightNumber())
                        .departureAirport(f.getDepartureAirport())
                        .departureTimeStamp(f.getDepartureTimeStamp())
                        .arrivalAirport(f.getArrivalAirport())
                        .arrivalTimeStamp(f.getArrivalTimeStamp())
                        .build())
                .collect(Collectors.toList());

        return TripResponseDTO.builder()
                .pnr(trip.getPnr())
                .cabinClass(trip.getCabinClass())
                .passengers(passengers)
                .flights(flights)
                .build();
    }

    /**
     * Publish PNR fetched event to Vert.x EventBus
     */
    private void publishPnrEvent(String pnr) {
        try {
            vertx.eventBus().publish("pnr.fetched", pnr);
            log.info("BookingAggregatorService | Published PNR fetched event for {}", pnr);
        } catch (Exception e) {
            log.error("BookingAggregatorService | Failed to publish PNR event for {}: {}", pnr, e.getMessage());
        }
    }
}

