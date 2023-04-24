package com.fligth_booking.fligth_booking_backend.flights.flightDTOs;

import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateFlightDTO {
    private String airline;
    private String flightCode;
    private List<SeatModel> seats = new ArrayList<>();
    private String departure;
    private String arrival;
    private String originAirportCode;
    private String destinationAirportCode;

}
