package com.fligth_booking.fligth_booking_backend.seats;

import com.fligth_booking.fligth_booking_backend.exceptions.OccupiedSeatException;
import com.fligth_booking.fligth_booking_backend.exceptions.SeatNoFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    SeatRepository seatRepository;

    @Transactional
    public void createSeat(SeatModel seat){
        seatRepository.save(seat);
    }

    @Transactional
    public void reserveSeat(Long seatId) throws OccupiedSeatException, SeatNoFoundException {
        Optional<SeatModel> seatOptional = seatRepository.findById(seatId);
        if (seatOptional.isEmpty()){
            throw new SeatNoFoundException(String.format("No seat found with the id %S", seatId));
        }
        SeatModel seat = seatOptional.get();
        if(seat.getSeatStatus() != SeatStatus.EMPTY){
            throw new OccupiedSeatException(String.format("The seat %s%s is occupied or blocked", seat.getRow(), seat.getCol()));
        }
        seat.setSeatStatus(SeatStatus.RESERVED);
        seatRepository.save(seat);
    }

    @Transactional
    public void vacateSeat(Long seatId) throws SeatNoFoundException {
        Optional<SeatModel> seatOptional = seatRepository.findById(seatId);
        if (seatOptional.isEmpty()){
            throw new SeatNoFoundException(String.format("No seat found with the id %S", seatId));
        }
        SeatModel seat = seatOptional.get();
        seat.setSeatStatus(SeatStatus.EMPTY);
        seatRepository.save(seat);
    }
}
