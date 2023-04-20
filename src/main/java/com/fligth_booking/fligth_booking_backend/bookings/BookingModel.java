package com.fligth_booking.fligth_booking_backend.bookings;

import com.fligth_booking.fligth_booking_backend.users.UserModel;
import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "booking")
@Getter@Setter@EqualsAndHashCode
public class BookingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private SeatModel seat;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false)
    private Date bookingDate;
}
