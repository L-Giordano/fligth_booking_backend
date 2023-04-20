package com.fligth_booking.fligth_booking_backend.flights;

import com.fligth_booking.fligth_booking_backend.flights.flightDTOs.FlightBasicInfoDTO;
import com.fligth_booking.fligth_booking_backend.users.UserModel;
import com.fligth_booking.fligth_booking_backend.users.userDTOs.UserBasicInfoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FlightService {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    ModelMapper modelMapper;
    public List<FlightBasicInfoDTO> allActiveFlights(){
        List<FlightBasicInfoDTO> flightBasicInfoDTO = new ArrayList<>();
        List<FlightModel> entities = flightRepository.findAllByStatusAndDepartureIsLessThan(Boolean.TRUE, new Date());
        if (entities.isEmpty()){
            return null;
        }
        for (FlightModel flight: entities
        ) {
            flightBasicInfoDTO.add(modelMapper.map(flight, FlightBasicInfoDTO.class));
        }
        return flightBasicInfoDTO;
    }


}
