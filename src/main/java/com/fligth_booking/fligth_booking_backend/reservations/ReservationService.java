package com.fligth_booking.fligth_booking_backend.reservations;

import com.fligth_booking.fligth_booking_backend.exceptions.OccupiedSeatException;
import com.fligth_booking.fligth_booking_backend.exceptions.ReservationNotFoundException;
import com.fligth_booking.fligth_booking_backend.exceptions.SeatNoFoundException;
import com.fligth_booking.fligth_booking_backend.flights.FlightModel;
import com.fligth_booking.fligth_booking_backend.reservations.reservationsDTOs.ReservationInfoDto;
import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import com.fligth_booking.fligth_booking_backend.seats.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SeatService seatService;

    @Transactional
    public URI createReservation(ReservationModel reservationModel) throws SeatNoFoundException, OccupiedSeatException {
        reservationModel.setStatus(Boolean.TRUE);
        reservationModel.setReservationStatus(ReservationStatus.ACTIVE);
        reservationModel.setBookingDate(ZonedDateTime.now());
        while (true){
            String reservationCode = this.createReservationCode();
            if (reservationRepository.findByReservationCode(reservationCode).isEmpty()){
                reservationModel.setReservationCode(reservationCode);
                break;
            }
        }
        seatService.reserveSeat(reservationModel.getSeat().getId());
        reservationRepository.save(reservationModel);

        return ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(reservationModel.getId())
            .toUri();
    }

    public String createReservationCode(){
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randomValue = random.nextInt(26);
            char randomLetter = (char) (randomValue + 65);
            builder.append(randomLetter);
        }
        return String.valueOf(builder);
    }

    public ReservationInfoDto getReservationByCode(Long id, String reservationCode) throws ReservationNotFoundException {
        Optional<ReservationModel> reservationOptional = reservationRepository.findByReservationCodeAndUserId(reservationCode, id);
        if(reservationOptional.isEmpty()){
            throw new ReservationNotFoundException(String.format("Reservation %s not found for the id %s", reservationCode, id));
        }
        ReservationModel reservation = reservationOptional.get();
        SeatModel seat = reservation.getSeat();
        FlightModel flight = seat.getFlight();

        ReservationInfoDto response = new ReservationInfoDto();
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.map(flight, response);
        modelMapper.map(seat, response);
        modelMapper.map(reservation, response);

        return response;
    }

    @Transactional
    public void cancelReservation(Long id) throws ReservationNotFoundException, SeatNoFoundException {
        Optional<ReservationModel> reservationOptional = reservationRepository.findById(id);
        if (reservationOptional.isEmpty()){
            throw new ReservationNotFoundException(String.format("Reservation %s not found", id));
        }
        ReservationModel reservation = reservationOptional.get();
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        seatService.vacateSeat(reservation.getSeat().getId());
        reservationRepository.save(reservation);
    }
}
