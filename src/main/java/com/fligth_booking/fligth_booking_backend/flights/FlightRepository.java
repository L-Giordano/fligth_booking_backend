package com.fligth_booking.fligth_booking_backend.flights;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<FlightModel, Long> {
    public abstract List<FlightModel> findAllByStatus(Boolean status);
    public abstract List<FlightModel> findAllByStatusAndDepartureIsLessThan(Boolean Status, Date date);
}
