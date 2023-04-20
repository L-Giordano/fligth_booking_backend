package com.fligth_booking.fligth_booking_backend.users.userDTOs;

import com.fligth_booking.fligth_booking_backend.bookings.BookingModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserBasicInfoDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private Boolean status;
}
