package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequestDTO {
    private String flightNumber;
    private String departureAirport;
    private String departureTimeStamp;
    private String arrivalAirport;
    private String arrivalTimeStamp;
}