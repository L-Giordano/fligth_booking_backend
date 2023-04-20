package com.fligth_booking.fligth_booking_backend.exceptions;

public class UserIdNotFoundException extends Exception{
    public UserIdNotFoundException  (String errorMessage) {
        super(errorMessage);
    }
}
