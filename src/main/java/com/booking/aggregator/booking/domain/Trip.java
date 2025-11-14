package com.booking.aggregator.booking.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "trip")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {
    @Id
    private String id; // auto-generated
    private String pnr;
    private String cabinClass;
    private List<Flight> flights;        // Embedded flights

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Flight {
        private String flightNumber;
        private String departureAirport;
        private String departureTimeStamp;
        private String arrivalAirport;
        private String arrivalTimeStamp;
    }
}
