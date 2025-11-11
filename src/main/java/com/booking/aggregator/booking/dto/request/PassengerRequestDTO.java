package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerRequestDTO {
    private int passengerNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String customerId;
    private String seat;
    private BaggageAllowanceRequestDTO baggageAllowance;
    private String ticketUrl; // optional
}