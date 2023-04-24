package com.fligth_booking.fligth_booking_backend.reservations.reservationsDTOs;

import com.fligth_booking.fligth_booking_backend.reservations.ReservationStatus;
import com.fligth_booking.fligth_booking_backend.seats.SeatType;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ReservationInfoDto {
    private String airline;
    private String flightCode;
    private ZonedDateTime departure;
    private ZonedDateTime arrival;
    private String originAirportCode;
    private String destinationAirportCode;
    private String row;
    private String col;
    private SeatType seatType;
    private String reservationCode;
    private ReservationStatus reservationStatus;


}
