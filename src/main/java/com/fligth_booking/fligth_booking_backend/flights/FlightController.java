package com.fligth_booking.fligth_booking_backend.flights;

import com.fligth_booking.fligth_booking_backend.exceptions.FlightIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.exceptions.InvalidSeatKeyPatternException;
import com.fligth_booking.fligth_booking_backend.flights.flightDTOs.CreateFlightDTO;
import com.fligth_booking.fligth_booking_backend.flights.flightDTOs.FlightBasicInfoDTO;
import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    FlightService flightService;

    @GetMapping
    public ResponseEntity<Object> getAllFlight(){
        try {
            List<FlightBasicInfoDTO> response = flightService.allActiveFlights();
            if (response.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFlightById(@PathVariable Long id){
        try {
            FlightBasicInfoDTO flightResponse = flightService.getActiveFlightById(id);
            return ResponseEntity.ok(flightResponse);
        }catch (FlightIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/byDepartureAndArrival")
    public ResponseEntity<Object> getFlightsByDepartureAndArrival(
            @RequestParam("arrival")LocalDate arrival,
            @RequestParam("departure")LocalDate departure,
            @RequestParam("destinationAirportCode") String destinationAirportCode,
            @RequestParam("originAirportCode") String originAirportCode,
            @RequestParam(value = "pagSize", required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pagNum", required = false, defaultValue = "0") Integer pageNum,
            @RequestParam(value = "status", required = false, defaultValue = "true") Boolean status
            )
    {
        try {
            Page<FlightModel> response = flightService.allFlightsByDepartureArrival(
                    pageNum,
                    pageSize,
                    originAirportCode,
                    destinationAirportCode,
                    departure,
                    arrival,
                    status);
            if (response.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @GetMapping("/{id}/getSeats")
    public ResponseEntity<Object> getAllSeats(@PathVariable Long id) {
        try{
            List<SeatModel> response = flightService.allSeats(id);

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createFlight(@RequestBody CreateFlightDTO createFlightDTO){
        //TODO:crear asientos, verificar que el vuelo no tenga id asignada
        try {
            URI uri = flightService.createFlight(createFlightDTO);
            return ResponseEntity.created(uri).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/createFlightSeats")
    public ResponseEntity<Object> createFlightSeats(
            @PathVariable Long id,
            @RequestBody HashMap<String, ArrayList<String>> seats)
    {
        try{
            flightService.createSeats(id, seats);
            return ResponseEntity.ok().build();
        }catch (FlightIdNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidSeatKeyPatternException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<Object> updateUser(@RequestBody FlightModel flightModel){
        try {
            flightService.updateFlight(flightModel);
            return ResponseEntity.noContent().build();
        }catch (FlightIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id){
        try {
            flightService.deleteFlight(id);
            return ResponseEntity.noContent().build();
        }catch (FlightIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
