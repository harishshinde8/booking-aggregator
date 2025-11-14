package com.booking.aggregator.booking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Allowance unit is required")
    private String allowanceUnit;

    @Min(value = 0, message = "Checked allowance value cannot be negative")
    private int checkedAllowanceValue;

    @Min(value = 0, message = "Carry-on allowance value cannot be negative")
    private int carryOnAllowanceValue;
}
