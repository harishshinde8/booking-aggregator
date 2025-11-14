package com.booking.aggregator.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "passenger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    private String id; // auto-generated
    private String pnr;
    private int passengerNumber;
    private String firstName;
    private String middleName;
    private String lastName;
    private String customerId;
    private String seat;
}
