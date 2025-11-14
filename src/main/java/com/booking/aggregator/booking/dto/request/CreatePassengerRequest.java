package com.booking.aggregator.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePassengerRequest {

    @NotNull(message = "Passenger number is required")
    private Integer passengerNumber;

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Seat is required")
    private String seat;

    private String ticketUrl; // optional

    private BaggageAllowanceRequest baggageAllowance; // optional
}
