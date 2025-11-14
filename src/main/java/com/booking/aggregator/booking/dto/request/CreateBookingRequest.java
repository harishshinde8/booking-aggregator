package com.booking.aggregator.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotBlank(message = "Cabin class is required")
    private String cabinClass;

    @NotEmpty(message = "Passengers list cannot be empty")
    private List<CreatePassengerRequest> passengers;

    @NotEmpty(message = "Flights list cannot be empty")
    private List<CreateFlightRequest> flights;
}
