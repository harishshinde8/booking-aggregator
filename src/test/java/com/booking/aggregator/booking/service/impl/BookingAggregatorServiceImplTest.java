package com.booking.aggregator.booking.service.impl;

import com.booking.aggregator.booking.domain.*;
import com.booking.aggregator.booking.dto.response.*;
import com.booking.aggregator.booking.exception.NotFoundException;
import com.booking.aggregator.booking.repository.*;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class BookingAggregatorServiceImplTest {

    @Mock
    private TripRepository tripRepo;
    @Mock
    private PassengerRepository passengerRepo;
    @Mock
    private BaggageRepository baggageRepo;
    @Mock
    private ETicketRepository eTicketRepo;
    @Mock
    private Vertx vertx;
    @Mock
    private EventBus eventBus;

    @InjectMocks
    private BookingAggregatorServiceImpl bookingAggregatorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(vertx.eventBus()).thenReturn(eventBus);
    }

    // -------------------------------------------------------------------------
    // TEST: Successful booking fetch
    // -------------------------------------------------------------------------
    @Test
    void testGetBooking_success() {
        String pnr = "PNR123";

        Trip trip = Trip.builder()
                .pnr(pnr)
                .cabinClass("ECONOMY")
                .flights(List.of(Trip.Flight.builder()
                        .flightNumber("XY101")
                        .arrivalAirport("DXB")
                        .departureAirport("BOM")
                        .arrivalTimeStamp("10:00")
                        .departureTimeStamp("06:00")
                        .build()))
                .build();

        Passenger passenger = Passenger.builder()
                .passengerNumber(1)
                .customerId("C1")
                .firstName("John")
                .lastName("Doe")
                .seat("12A")
                .pnr(pnr)
                .build();

        Baggage baggage = Baggage.builder()
                .passengerNumber(1)
                .carryOnAllowanceValue(7)
                .checkedAllowanceValue(20)
                .allowanceUnit("KG")
                .build();

        ETicket ticket = ETicket.builder()
                .id("T1")
                .passengerNumber(1)
                .ticketUrl("http://ticket.com/123")
                .build();

        when(tripRepo.findByPnr(pnr)).thenReturn(Mono.just(trip));
        when(passengerRepo.findByPnr(pnr)).thenReturn(Flux.just(passenger));
        when(baggageRepo.findByPassengerNumber(1)).thenReturn(Flux.just(baggage));
        when(eTicketRepo.findByPassengerNumber(1)).thenReturn(Mono.just(ticket));

        StepVerifier.create(bookingAggregatorService.getBooking(pnr))
                .assertNext(res -> {
                    assert res.getPnr().equals(pnr);
                    assert res.getPassengers().size() == 1;

                    PassengerResponseDTO p = res.getPassengers().get(0);
                    assert p.getFullName().equals("John Doe");
                    assert p.getSeat().equals("12A");
                    assert p.getBaggageAllowance().size() == 1;
                    assert p.getTicketUrl().equals("http://ticket.com/123");

                    assert res.getFlights().size() == 1;
                })
                .verifyComplete();

        verify(eventBus, times(1)).publish("pnr.fetched", pnr);
    }

    // -------------------------------------------------------------------------
    // TEST: PNR Not Found → NotFoundException
    // -------------------------------------------------------------------------
    @Test
    void testGetBooking_pnrNotFound() {
        String pnr = "INVALID";

        when(tripRepo.findByPnr(pnr)).thenReturn(Mono.empty());

        StepVerifier.create(bookingAggregatorService.getBooking(pnr))
                .expectErrorMatches(ex ->
                        ex instanceof NotFoundException &&
                                ex.getMessage().contains("PNR not found"))
                .verify();

        verify(eventBus, never()).publish(anyString(), any());
    }

    // -------------------------------------------------------------------------
    // TEST: Unexpected Repo Error → Wrapped RuntimeException
    // -------------------------------------------------------------------------
    @Test
    void testGetBooking_repoError() {
        String pnr = "XYZ";

        when(tripRepo.findByPnr(pnr)).thenReturn(Mono.error(new RuntimeException("DB failure")));

        StepVerifier.create(bookingAggregatorService.getBooking(pnr))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                                ex.getMessage().contains("Internal server error"))
                .verify();
    }


}
