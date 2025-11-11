package com.booking.aggregator.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerResponseDTO {
    private int passengerNumber;
    private String customerId;
    private String fullName;
    private String seat;
    private String ticketUrl;
    private BaggageAllowanceResponseDTO baggageAllowance;
}
