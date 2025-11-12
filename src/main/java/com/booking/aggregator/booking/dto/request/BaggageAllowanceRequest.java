package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single baggage allowance item for a passenger.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaggageAllowanceRequest {
    private String allowanceUnit;
    private int checkedAllowanceValue;
    private int carryOnAllowanceValue;
}
