package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request to create a complete booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    private String cabinClass;
    private List<CreatePassengerRequest> passengers;
    private List<CreateFlightRequest> flights;
}
