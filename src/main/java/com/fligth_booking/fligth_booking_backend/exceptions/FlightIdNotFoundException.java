package com.fligth_booking.fligth_booking_backend.exceptions;

public class FlightIdNotFoundException extends Exception{
    public FlightIdNotFoundException  (String errorMessage) {
        super(errorMessage);
    }
}
