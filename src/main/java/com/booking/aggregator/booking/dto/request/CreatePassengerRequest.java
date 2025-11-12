package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePassengerRequest {

    private int passengerNumber;
    private String customerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String seat;
    private String ticketUrl; // optional
    private BaggageAllowanceRequest baggageAllowance; // optional list
}
