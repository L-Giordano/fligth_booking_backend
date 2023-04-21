package com.fligth_booking.fligth_booking_backend.flights;

import com.fligth_booking.fligth_booking_backend.users.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<FlightModel, Long> {
    public abstract List<FlightModel> findAllByStatus(Boolean status);
    public abstract List<FlightModel> findAllByStatusAndDepartureDateIsGreaterThan(Boolean Status, LocalDate date);

    public abstract Optional<FlightModel> findByIdAndStatus(Long id, Boolean status);

    @Query(value= "SELECT f FROM FlightModel f WHERE " +
            "f.originAirportCode = :originAirportCode " +
            "AND f.destinationAirportCode = :destinationAirportCode " +
            "AND f.departureDate = :departure " +
            "AND f.arrivalDate = :arrival " +
            "AND f.status = :status")
    Page<FlightModel> findFlightsByDepartureArrivalOriginDestination(
            @Param("originAirportCode") String originAirportCode,
            @Param("destinationAirportCode") String destinationAirportCode,
            @Param("departure") LocalDate departure,
            @Param("arrival") LocalDate arrival,
            @Param("status") Boolean status,
            @Param("paging") Pageable pageable
    );
}
