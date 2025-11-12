package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFlightRequest {
    private String flightNumber;
    private String departureAirport;
    private String departureTimeStamp;
    private String arrivalAirport;
    private String arrivalTimeStamp;
}

