package com.fligth_booking.fligth_booking_backend.reservations;

import com.fligth_booking.fligth_booking_backend.users.UserModel;
import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "booking")
@Getter@Setter@EqualsAndHashCode
public class ReservationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserModel user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private SeatModel seat;

    @Column
    private String reservationCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private ZonedDateTime bookingDate;

    @Column(nullable = false)
    private Boolean status;
}
