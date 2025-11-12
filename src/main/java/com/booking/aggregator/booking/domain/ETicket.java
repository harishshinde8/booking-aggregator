package com.booking.aggregator.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ETicket {
    @Id
    private String id; // auto-generated
    private int passengerNumber;
    private String ticketUrl;
}

