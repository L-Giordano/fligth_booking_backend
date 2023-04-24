package com.fligth_booking.fligth_booking_backend.exceptions;

public class OccupiedSeatException extends Exception{
    public OccupiedSeatException(String errorMessage) {
        super(errorMessage);
    }
}
