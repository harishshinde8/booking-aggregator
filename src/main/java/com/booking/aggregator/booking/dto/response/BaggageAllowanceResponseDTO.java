package com.booking.aggregator.booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaggageAllowanceResponseDTO {
    private int passengerNumber;
    private String allowanceUnit;
    private int checkedAllowanceValue;
    private int carryOnAllowanceValue;
}