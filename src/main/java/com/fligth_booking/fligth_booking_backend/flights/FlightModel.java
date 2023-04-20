package com.fligth_booking.fligth_booking_backend.flights;

import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "flight")
@Getter
@Setter
@EqualsAndHashCode
public class FlightModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatModel> seats = new ArrayList<>();

    @Column(nullable = false)
    private Date departure;

    @Column(nullable = false)
    private Date arrival;

    @Column(nullable = false)
    private String originCity;

    @Column(nullable = false)
    private String originCountry;

    @Column(nullable = false)
    private String originAirport;

    @Column(nullable = false)
    private String originAirportCode;

    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private String destinationCountry;

    @Column(nullable = false)
    private String destinationAirport;

    @Column(nullable = false)
    private String destinationAirportCode;

    @Column(nullable = false)
    private Boolean status;




}
