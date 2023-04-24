package com.fligth_booking.fligth_booking_backend.reservations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationModel, Long> {
    public abstract Optional<ReservationModel> findByReservationCode(String reservationCode);
    public abstract Optional<ReservationModel> findByReservationCodeAndUserId(String reservationCode, Long id);
}
