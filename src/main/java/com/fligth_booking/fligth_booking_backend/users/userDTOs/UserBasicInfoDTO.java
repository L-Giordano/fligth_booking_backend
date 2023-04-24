package com.fligth_booking.fligth_booking_backend.users.userDTOs;

import lombok.Data;

@Data
public class UserBasicInfoDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private Boolean status;
}
