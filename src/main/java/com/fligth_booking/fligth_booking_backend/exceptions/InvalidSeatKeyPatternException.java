package com.fligth_booking.fligth_booking_backend.exceptions;

public class InvalidSeatKeyPatternException extends Exception{
    public InvalidSeatKeyPatternException(String errorMessage) {
        super(errorMessage);
    }
}
