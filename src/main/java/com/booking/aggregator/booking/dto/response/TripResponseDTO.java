package com.booking.aggregator.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponseDTO {
    private String pnr;
    private String cabinClass;
    private List<PassengerResponseDTO> passengers;
    private List<FlightResponseDTO> flights;
}