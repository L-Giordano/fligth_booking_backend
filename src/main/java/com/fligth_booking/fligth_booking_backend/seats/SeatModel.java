package com.fligth_booking.fligth_booking_backend.seats;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fligth_booking.fligth_booking_backend.flights.FlightModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "seat")
@Getter
@Setter
@EqualsAndHashCode
public class SeatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String row;

    @Column(nullable = false)
    private String col;

    @Column(nullable = false)
    private Boolean status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id")
    private FlightModel flight;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus seatStatus;

    @Column(nullable = false)
    private Float price;
}
