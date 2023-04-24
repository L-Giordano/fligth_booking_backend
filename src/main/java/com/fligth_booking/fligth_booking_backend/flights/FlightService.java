package com.fligth_booking.fligth_booking_backend.flights;

import com.fligth_booking.fligth_booking_backend.exceptions.FlightIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.exceptions.InvalidSeatKeyPatternException;
import com.fligth_booking.fligth_booking_backend.flights.flightDTOs.CreateFlightDTO;
import com.fligth_booking.fligth_booking_backend.flights.flightDTOs.FlightBasicInfoDTO;
import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import com.fligth_booking.fligth_booking_backend.seats.SeatService;
import com.fligth_booking.fligth_booking_backend.seats.SeatStatus;
import com.fligth_booking.fligth_booking_backend.seats.SeatType;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class FlightService {

    @Autowired
    FlightRepository flightRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SeatService seatService;

    public List<FlightBasicInfoDTO> allActiveFlights(){
        List<FlightBasicInfoDTO> flightBasicInfoDTOList = new ArrayList<>();
        List<FlightModel> entities = flightRepository.findAllByStatusAndDepartureDateIsGreaterThan(Boolean.TRUE, LocalDate.now());
        if (entities.isEmpty()){
            return flightBasicInfoDTOList;
        }
        for (FlightModel flight: entities
        ) {
            flightBasicInfoDTOList.add(modelMapper.map(flight, FlightBasicInfoDTO.class));
        }
        return flightBasicInfoDTOList;
    }

    public FlightBasicInfoDTO getActiveFlightById(Long id) throws FlightIdNotFoundException {
        Optional<FlightModel> flightOptional = flightRepository.findByIdAndStatus(id, Boolean.TRUE);
        if(flightOptional.isEmpty()){
            throw new FlightIdNotFoundException(String.format("User Id %s not Found", id));
        }
        return modelMapper.map(flightOptional.get(), FlightBasicInfoDTO.class);
    }

    @Transactional
    public URI createFlight(CreateFlightDTO createFlightDTO){
        FlightModel flightModel = modelMapper.map(createFlightDTO, FlightModel.class);
        flightModel.setDeparture(this.createZoneDateTime(createFlightDTO.getDeparture()));
        flightModel.setArrival(this.createZoneDateTime(createFlightDTO.getArrival()));
        flightModel.setStatus(Boolean.TRUE);
        flightModel.setDepartureDate(flightModel.getDeparture().toLocalDate());
        flightModel.setArrivalDate(flightModel.getArrival().toLocalDate());
        flightModel.setFlightCode(this.createFlightCode(flightModel.getAirline()));
        flightRepository.save(flightModel);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(flightModel.getId())
                .toUri();
    }

    public ZonedDateTime createZoneDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy, MM, dd, HH, mm, ss, SS, zzz");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(date, formatter);
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);

    }

    public String createFlightCode(String airline){
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int randomValue = random.nextInt(26);
            char randomLetter = (char) (randomValue + 65);
            builder.append(randomLetter);
        }

        String airlineName = airline.replaceAll(" ","").toUpperCase();
        return airlineName +"-"+ builder;
    }

    public Page<FlightModel> allFlightsByDepartureArrival(
            Integer pageNum,
            Integer pageSize,
            String originAirportCode,
            String destinationAirportCode,
            LocalDate departure,
            LocalDate arrival,
            Boolean status){
        Pageable paging = PageRequest.of(pageNum, pageSize);
        return flightRepository.findFlightsByDepartureArrivalOriginDestination(
                originAirportCode,
                destinationAirportCode,
                departure,
                arrival,
                status,
                paging);
    }

    public List<SeatModel> allSeats(Long id) throws FlightIdNotFoundException {
        Optional<FlightModel> flightOptional = flightRepository.findById(id);
        if(flightOptional.isEmpty()){
            throw new FlightIdNotFoundException(String.format("Flight Id %s not Found", id));
        }

        FlightModel flight = flightOptional.get();

        return flight.getSeats();
    }

    public void updateFlight(FlightModel flightModel) throws FlightIdNotFoundException {
        Optional<FlightModel> response = flightRepository.findById(flightModel.getId());
        if(response.isEmpty()){
            throw new FlightIdNotFoundException(String.format("Flight Id %s not Found", flightModel.getId()));
        }
        flightRepository.save(flightModel);
    }

    public void deleteFlight(Long id) throws FlightIdNotFoundException {
        //TODO:cancelar reserva
        Optional<FlightModel> responseOptional = flightRepository.findById(id);
        if(responseOptional.isEmpty()){
            throw new FlightIdNotFoundException(String.format("Flight Id %s not Found", id));
        }
        FlightModel flightModel = responseOptional.get();
        //TODO:It might be a good idea to check if the user has active bookings before deactivate it
        flightModel.setStatus(Boolean.FALSE);
        flightRepository.save(flightModel);
    }

    public void createSeats(Long id, HashMap<String, ArrayList<String>> seats) throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        Optional<FlightModel> flightoptional = flightRepository.findById(id);
        if(flightoptional.isEmpty()){
            throw new FlightIdNotFoundException(String.format("Flight Id %s not Found", id));
        }
        FlightModel flight = flightoptional.get();

        for (Map.Entry<String, ArrayList<String>> entry : seats.entrySet()) {
            String seatPosition = entry.getKey();
            if(!isValidateKeyPattern(seatPosition)){
                throw new InvalidSeatKeyPatternException("The key pattern is invalid. It must be ^[A-Z]+-\\d+-[A-Z\\s]+$");
            }
            Float price = Float.parseFloat(entry.getValue().get(1));
            SeatType seatType = SeatType.valueOf(entry.getValue().get(0));
            String col = seatPosition.replaceAll("[^\\d.]", "");
            String row = seatPosition.replaceAll("[\\d.]", "");

            SeatModel seat = new SeatModel();
            seat.setCol(col);
            seat.setRow(row);
            seat.setFlight(flight);
            seat.setSeatType(seatType);
            seat.setSeatStatus(SeatStatus.EMPTY);
            seat.setStatus(true);
            seat.setPrice(price);
            seatService.createSeat(seat);
        }
    }

    public Boolean isValidateKeyPattern(String key){
        String pattern = "^[A-Z]+\\d+$";
        return key.matches(pattern);
    }


}
