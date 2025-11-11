package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    private String pnr;
    private String cabinClass;
    private List<PassengerRequestDTO> passengers;
    private List<FlightRequestDTO> flights;
}
