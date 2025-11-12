# Booking Aggregator Service

A Spring Boot + Vert.x reactive backend service for aggregating booking information (PNR, passengers, flights, baggage, and e-tickets) with real-time WebSocket updates.

## Features
- Reactive backend using Spring WebFlux and Vert.x
- MongoDB integration (Reactive)
- REST API for creating and fetching booking PNRs
- Vert.x EventBus for internal event communication
- WebSocket for real-time notifications when a PNR is fetched
- Swagger UI for API documentation
- Dockerized application and MongoDB setup via Docker Compose

## Tech Stack
- Java 21
- Spring Boot 3.5.7
- Spring WebFlux
- Spring Data Reactive MongoDB
- Vert.x
- Maven
- Docker & Docker Compose

## Getting Started

### Prerequisites
- Java 21 installed (for local run)
- Docker & Docker Compose installed
- MongoDB (local or via Docker Compose)

### Running Locally
1. Clone the repo
   ```bash
   git clone https://github.com/harishshinde8/booking-aggregator.git
   cd booking-aggregator
2. Update application.yml if needed:
   ```bash
   spring:
     data: 
       mongodb:
         uri: mongodb://localhost:27017/pnrDB
   server:
    port: 8889
3. Build and run:
   ```bash
   ./mvnw clean package
    java -jar target/booking-aggregator-0.0.1-SNAPSHOT.jar
4. Swagger UI: http://localhost:8889/swagger-ui.html
   REST API base URL: http://localhost:8889

Using Docker
1. Build the Docker image:
   ```bash
   docker build -t booking:latest .
   
2. Run with Docker Compose:
   ```bash
   docker-compose up --build
* REST API port: 8888
* WebSocket port: 9999
* MongoDB port: 27017

API Endpoints

Create Booking PNR
* POST /create
* Request Body:
```json
{
   "cabinClass": "ECONOMY",
   "passengers": [
      {
         "passengerNumber": 1,
         "customerId": "1218",
         "fullName": "James Morgan McGill",
         "seat": "32D",
         "ticketUrl": null,
         "baggageAllowance": {
            "allowanceUnit": "kg",
            "checkedAllowanceValue": 25,
            "carryOnAllowanceValue": 7
         }
      },
      {
         "passengerNumber": 2,
         "customerId": "1219",
         "fullName": "Charles McGill",
         "seat": "31D",
         "ticketUrl": "emirates.com?ticket=someTicketRef",
         "baggageAllowance": {
            "allowanceUnit": "kg",
            "checkedAllowanceValue": 25,
            "carryOnAllowanceValue": 7
         }
      }
   ],
   "flights": [
      {
         "flightNumber": "EK231",
         "departureAirport": "DXB",
         "departureTimeStamp": "2025-11-11T02:25:00+00:00",
         "arrivalAirport": "IAD",
         "arrivalTimeStamp": "2025-11-11T08:10:00+00:00"
      },
      {
         "flightNumber": "EK123",
         "departureAirport": "IAD",
         "departureTimeStamp": "2025-11-11T02:25:00+00:00",
         "arrivalAirport": "DXB",
         "arrivalTimeStamp": "2025-11-11T08:10:00+00:00"
      }
   ]
}
```
Get Booking by PNR

* GET /{pnr}
* Path Parameter: pnr (string)
* Response: Trip details with passengers, flights, baggage, and tickets

WebSocket

* Endpoint: ws://localhost:9999/pnr-updates
* Sends real-time notifications whenever a PNR is fetched

Environment Variables

* SPRING_DATA_MONGODB_URI – MongoDB connection string (default: mongodb://localhost:27017/pnrDB)
* SERVER_PORT – Application port (default: 8889)
* VERTX_WS_PORT – WebSocket port (default: 9999)
  
Notes

* Ensure MongoDB is running locally or via Docker before starting the service
* Use Docker Compose to start both MongoDB and the application together
* Swagger UI provides a convenient way to test API endpoints