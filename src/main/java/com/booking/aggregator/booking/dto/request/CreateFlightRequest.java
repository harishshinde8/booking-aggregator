package com.booking.aggregator.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightRequest {

    @NotBlank(message = "Flight number is required")
    private String flightNumber;

    @NotBlank(message = "Departure airport is required")
    private String departureAirport;

    @NotBlank(message = "Departure timestamp is required")
    private String departureTimeStamp;

    @NotBlank(message = "Arrival airport is required")
    private String arrivalAirport;

    @NotBlank(message = "Arrival timestamp is required")
    private String arrivalTimeStamp;
}


