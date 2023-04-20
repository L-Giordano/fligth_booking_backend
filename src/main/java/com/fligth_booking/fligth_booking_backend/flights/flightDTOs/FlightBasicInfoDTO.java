package com.fligth_booking.fligth_booking_backend.flights.flightDTOs;

import lombok.Data;

import java.util.Date;

@Data
public class FlightBasicInfoDTO {
        private Long id;
        private String originAirportCode;
        private String destinationAirportCode;
        private Date departure;
        private Date arrival;
        private Boolean status;

}
