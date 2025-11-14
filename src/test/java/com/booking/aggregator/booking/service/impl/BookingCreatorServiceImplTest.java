package com.booking.aggregator.booking.service.impl;

import com.booking.aggregator.booking.domain.*;
import com.booking.aggregator.booking.dto.request.*;
import com.booking.aggregator.booking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class BookingCreatorServiceImplTest {

    @Mock
    private TripRepository tripRepo;
    @Mock
    private PassengerRepository passengerRepo;
    @Mock
    private BaggageRepository baggageRepo;
    @Mock
    private ETicketRepository eTicketRepo;

    @InjectMocks
    private BookingCreatorServiceImpl bookingCreatorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------------------
    // TEST: Successful booking creation
    // -------------------------------------------------------------------------
    @Test
    void testCreateBooking_success() {
        BaggageAllowanceRequest baggageReq =
                BaggageAllowanceRequest.builder()
                        .allowanceUnit("KG")
                        .carryOnAllowanceValue(7)
                        .checkedAllowanceValue(20)
                        .build();

        CreatePassengerRequest passengerReq =
                CreatePassengerRequest.builder()
                        .passengerNumber(1)
                        .firstName("John")
                        .lastName("Doe")
                        .customerId("C1")
                        .seat("12A")
                        .baggageAllowance(baggageReq)
                        .ticketUrl("http://ticket.com/123")
                        .build();

        CreateFlightRequest flightReq =
                new CreateFlightRequest("XY101", "BOM", "06:00", "DXB", "10:00");

        CreateBookingRequest request =
                CreateBookingRequest.builder()
                        .cabinClass("ECONOMY")
                        .flights(List.of(flightReq))
                        .passengers(List.of(passengerReq))
                        .build();

        // Mocks
        Trip savedTrip = new Trip();
        savedTrip.setPnr("PNR999");
        savedTrip.setCabinClass("ECONOMY");
        savedTrip.setFlights(List.of());

        when(tripRepo.save(any(Trip.class))).thenReturn(Mono.just(savedTrip));

        when(passengerRepo.save(any(Passenger.class))).thenAnswer(inv -> {
            Passenger p = inv.getArgument(0);
            p.setId("PID1");
            return Mono.just(p);
        });

        when(baggageRepo.save(any(Baggage.class))).thenAnswer(inv -> {
            Baggage b = inv.getArgument(0);
            b.setId("BID1");
            return Mono.just(b);
        });

        when(eTicketRepo.save(any(ETicket.class))).thenAnswer(inv -> {
            ETicket t = inv.getArgument(0);
            t.setId("TID1");
            return Mono.just(t);
        });

        StepVerifier.create(bookingCreatorService.createBooking(request))
                .assertNext(res -> {
                    assert res.getCabinClass().equals("ECONOMY");
                    assert res.getPnr() != null;
                })
                .verifyComplete();

        verify(tripRepo, times(1)).save(any(Trip.class));
        verify(passengerRepo, times(1)).save(any(Passenger.class));
        verify(baggageRepo, times(1)).save(any(Baggage.class));
        verify(eTicketRepo, times(1)).save(any(ETicket.class));
    }


    // -------------------------------------------------------------------------
    // TEST: PNR generator
    // -------------------------------------------------------------------------
    @Test
    void testGeneratePnr() {
        String pnr = BookingCreatorServiceImpl.generatePnr(7);

        assert pnr.length() == 7;
        assert pnr.matches("^[A-Za-z0-9]+$");
    }
}
