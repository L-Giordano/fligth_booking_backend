package com.fligth_booking.fligth_booking_backend.seats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {

    @Autowired
    SeatRepository seatRepository;

    @Transactional
    public void createSeat(SeatModel seat){
        seatRepository.save(seat);
    }
}
