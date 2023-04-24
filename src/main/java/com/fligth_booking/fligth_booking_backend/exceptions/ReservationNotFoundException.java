package com.fligth_booking.fligth_booking_backend.exceptions;

public class ReservationNotFoundException extends Exception{
    public ReservationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
