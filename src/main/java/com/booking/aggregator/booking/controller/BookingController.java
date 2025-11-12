package com.booking.aggregator.booking.controller;

import com.booking.aggregator.booking.dto.request.CreateBookingRequest;
import com.booking.aggregator.booking.dto.response.TripResponseDTO;
import com.booking.aggregator.booking.service.BookingAggregatorService;
import com.booking.aggregator.booking.service.BookingCreatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/booking")
@Slf4j
public class BookingController {

    private final BookingAggregatorService bookingAggregatorService;

    private final BookingCreatorService bookingCreatorService;

    public BookingController(BookingAggregatorService bookingAggregatorService, BookingCreatorService bookingCreatorService) {
        this.bookingAggregatorService = bookingAggregatorService;
        this.bookingCreatorService = bookingCreatorService;
    }

    @Operation(summary = "Get booking by PNR",
            description = "Fetch booking details for a given PNR")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found for the given PNR"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{pnr}")
    public Mono<ResponseEntity<TripResponseDTO>> getBooking(@PathVariable String pnr) {
        return bookingAggregatorService.getBooking(pnr)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @Operation(summary = "Create a new booking PNR",
            description = "Create PNR along with passengers, flights, baggage, and tickets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<String> createBooking(@RequestBody CreateBookingRequest request) {
        log.info("Creating booking for request: {}", request);
        return bookingCreatorService.createBooking(request).flatMap(trip -> Mono.just("Booking has been created successfully with PNR : "+trip.getPnr()));
    }
}
