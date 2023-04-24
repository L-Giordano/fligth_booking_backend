package com.fligth_booking.fligth_booking_backend.reservations;

import com.fligth_booking.fligth_booking_backend.exceptions.FlightIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.exceptions.ReservationNotFoundException;
import com.fligth_booking.fligth_booking_backend.exceptions.SeatNoFoundException;
import com.fligth_booking.fligth_booking_backend.reservations.reservationsDTOs.ReservationInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Object> postReservation(@RequestBody ReservationModel reservationModel){
        try {
            URI uri = reservationService.createReservation(reservationModel);
            return ResponseEntity.created(uri).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/getReservationByCode")
    public ResponseEntity<Object> getReservationByCode(
            @RequestParam("reservation") String reservationCode,
            @RequestParam("id") Long id) {
        try {
            ReservationInfoDto response = reservationService.getReservationByCode(id, reservationCode);
            return ResponseEntity.ok(response);
        }catch (ReservationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Object> cancelReservation(@PathVariable Long id){
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.noContent().build();
        }catch (ReservationNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (SeatNoFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
