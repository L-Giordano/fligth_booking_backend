package com.fligth_booking.fligth_booking_backend.exceptions;

public class SeatNoFoundException extends Exception{
    public SeatNoFoundException(String errorMessage) {
        super(errorMessage);
    }
}
