package com.booking.aggregator.booking.controller;

import com.booking.aggregator.booking.domain.Trip;
import com.booking.aggregator.booking.dto.request.CreateBookingRequest;
import com.booking.aggregator.booking.dto.response.TripResponseDTO;
import com.booking.aggregator.booking.service.BookingAggregatorService;
import com.booking.aggregator.booking.service.BookingCreatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingControllerTest {

    @Mock
    private BookingAggregatorService bookingAggregatorService;

    @Mock
    private BookingCreatorService bookingCreatorService;

    @InjectMocks
    private BookingController bookingController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(bookingController).build();
    }

    // -------------------------------------------------------------------------
    // GET /booking/{pnr} - success
    // -------------------------------------------------------------------------
    @Test
    void testGetBooking_success() {
        TripResponseDTO tripResponse = TripResponseDTO.builder()
                .pnr("PNR123")
                .cabinClass("ECONOMY")
                .passengers(List.of())
                .flights(List.of())
                .build();

        when(bookingAggregatorService.getBooking("PNR123"))
                .thenReturn(Mono.just(tripResponse));

        webTestClient.get()
                .uri("/booking/PNR123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(TripResponseDTO.class)
                .value(res -> {
                    assert res.getPnr().equals("PNR123");
                    assert res.getCabinClass().equals("ECONOMY");
                });
    }

    // -------------------------------------------------------------------------
    // GET /booking/{pnr} - not found
    // -------------------------------------------------------------------------
    @Test
    void testGetBooking_notFound() {
        when(bookingAggregatorService.getBooking("PNR_NOT_EXIST"))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/booking/PNR_NOT_EXIST")
                .exchange()
                .expectStatus().isNotFound();
    }

    // -------------------------------------------------------------------------
    // POST /booking/create - success
    // -------------------------------------------------------------------------
    @Test
    void testCreateBooking_success() {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .cabinClass("ECONOMY")
                .flights(List.of())
                .passengers(List.of())
                .build();

        TripResponseDTO tripResponse = TripResponseDTO.builder()
                .pnr("PNR123")
                .cabinClass("ECONOMY")
                .build();

        when(bookingCreatorService.createBooking(any()))
                .thenReturn(Mono.just(new Trip()));

        webTestClient.post()
                .uri("/booking/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(msg -> msg.contains("Booking has been created successfully with PNR : PNR123"));
    }
}
