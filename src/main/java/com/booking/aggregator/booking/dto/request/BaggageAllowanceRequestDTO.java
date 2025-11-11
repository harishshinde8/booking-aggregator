package com.booking.aggregator.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaggageAllowanceRequestDTO {
    private String allowanceUnit;
    private int checkedAllowanceValue;
    private int carryOnAllowanceValue;
}