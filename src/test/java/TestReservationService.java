import com.fligth_booking.fligth_booking_backend.exceptions.*;
import com.fligth_booking.fligth_booking_backend.flights.FlightModel;
import com.fligth_booking.fligth_booking_backend.reservations.ReservationModel;
import com.fligth_booking.fligth_booking_backend.reservations.ReservationRepository;
import com.fligth_booking.fligth_booking_backend.reservations.ReservationService;
import com.fligth_booking.fligth_booking_backend.reservations.ReservationStatus;
import com.fligth_booking.fligth_booking_backend.reservations.reservationsDTOs.ReservationInfoDto;
import com.fligth_booking.fligth_booking_backend.seats.*;
import com.fligth_booking.fligth_booking_backend.users.UserModel;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@Slf4j
public class TestReservationService {

    @InjectMocks
    ReservationService reservationService;

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    SeatService seatService;

    @BeforeClass
    public static void setupDateNow() {
        String instantExpected = String.valueOf(LocalDate.of(2023, 5, 8));
        LocalDate localDate = LocalDate.parse(instantExpected);
        MockedStatic<LocalDate> mockedLocalDateTime = Mockito.mockStatic(LocalDate.class);
        mockedLocalDateTime.when(LocalDate::now).thenReturn(localDate);
    }

    public UserModel setupUserToTest(Long id) {
        UserModel userTest = new UserModel();
        userTest.setId(id);
        userTest.setFirstName("User");
        userTest.setFirstName("User");
        userTest.setEmail(String.format("user%s@user.com", id));
        userTest.setPassword("1234");
        return userTest;
    }

    public FlightModel setupFlightToTest(Long id) throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flightTest = new FlightModel();
        flightTest.setId(id);
        flightTest.setAirline("airline");
        flightTest.setDeparture(ZonedDateTime.of(2023, 04, 18, 10, 0, 0, 0, ZoneId.of("UTC")));
        flightTest.setArrival(ZonedDateTime.of(2023, 04, 18, 12, 0, 0, 0, ZoneId.of("UTC")));
        flightTest.setOriginAirportCode("MDZ");
        flightTest.setDestinationAirportCode("AEP");
        flightTest.setFlightCode("CODER");

        HashMap<String, ArrayList<String>> seats = new HashMap<>();
        seats.put("A1", new ArrayList<>(Arrays.asList("FIRST_CLASS", "100.50")));
        seats.put("A2", new ArrayList<>(Arrays.asList("BUSINESS", "85.50")));
        seats.put("A2", new ArrayList<>(Arrays.asList("ECONOMIC", "100.00")));

        SeatModel seat1 = this.createSeat(flightTest, "A", "1", 100.5F, SeatType.FIRST_CLASS);
        SeatModel seat2 = this.createSeat(flightTest, "B", "1", 80.5F, SeatType.BUSINESS);
        SeatModel seat3 = this.createSeat(flightTest, "C", "1", 60.5F, SeatType.ECONOMIC);

        List<SeatModel> seatList = Arrays.asList(seat1, seat2, seat3);

        flightTest.setSeats(seatList);

        return flightTest;
    }

    public SeatModel createSeat(FlightModel flight, String row, String col, Float price, SeatType seatType) {
        SeatModel seat = new SeatModel();
        seat.setRow(row);
        seat.setCol(col);
        seat.setSeatStatus(SeatStatus.EMPTY);
        seat.setPrice(price);
        seat.setSeatType(seatType);
        seat.setStatus(true);
        seat.setFlight(flight);

        return seat;

    }

    public ReservationModel setupReservation(Long id) throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flight = this.setupFlightToTest(1L);
        SeatModel seat = flight.getSeats().get(0);
        UserModel user = this.setupUserToTest(1L);
        ReservationModel reservation = new ReservationModel();
        reservation.setId(id);
        reservation.setUser(user);
        reservation.setSeat(seat);
        reservation.setReservationCode("AXFTR");

        return reservation;
    }

    @Test
    public void testCreateReservationGoOk() throws FlightIdNotFoundException, InvalidSeatKeyPatternException, SeatNoFoundException, OccupiedSeatException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/reservations");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ReservationModel reservation = this.setupReservation(1L);

        doAnswer(invocation -> {
            reservation.getSeat().setSeatStatus(SeatStatus.RESERVED);
            return null;
        }).when(seatService).reserveSeat(reservation.getSeat().getId());

        URI uri = reservationService.createReservation(reservation);

        URI expectedUri = URI.create("http://localhost/reservations/" + reservation.getId());

        assertEquals(expectedUri, uri);
        assertTrue(reservation.getStatus());
        assertEquals(ReservationStatus.ACTIVE, reservation.getReservationStatus());
    }

    @Test
    public void testCreateReservation() throws FlightIdNotFoundException, InvalidSeatKeyPatternException, SeatNoFoundException, OccupiedSeatException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/reservations");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ReservationModel reservation = this.setupReservation(1L);

        doAnswer(invocation -> {
            reservation.getSeat().setSeatStatus(SeatStatus.RESERVED);
            return null;
        }).when(seatService).reserveSeat(reservation.getSeat().getId());

        URI uri = reservationService.createReservation(reservation);

        URI expectedUri = URI.create("http://localhost/reservations/" + reservation.getId());

        assertEquals(expectedUri, uri);
        assertTrue(reservation.getStatus());
        assertEquals(ReservationStatus.ACTIVE, reservation.getReservationStatus());
    }

    @Test
    public void testCancelReservation() throws FlightIdNotFoundException, InvalidSeatKeyPatternException, SeatNoFoundException, ReservationNotFoundException {
        ReservationModel reservation = this.setupReservation(1L);

        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(reservation.getId());

        assertEquals(ReservationStatus.CANCELLED, reservation.getReservationStatus());
        verify(seatService, times(1)).vacateSeat(reservation.getSeat().getId());
        verify(reservationRepository, times(1)).save(reservation);

    }

    @Test(expected = ReservationNotFoundException.class)
    public void testCancelNoExistingReservation() throws SeatNoFoundException, ReservationNotFoundException {
        reservationService.cancelReservation(1L);
    }

    @Test(expected = ReservationNotFoundException.class)
    public void testGetNoExistingReservationByReservationCode() throws ReservationNotFoundException {
        reservationService.getReservationByCode(1l, "ADFRT");
    }

    @Test
    public void testGetExistingReservationByReservationId() throws FlightIdNotFoundException, InvalidSeatKeyPatternException, ReservationNotFoundException {

        ReservationModel reservation = this.setupReservation(1l);

        when(reservationRepository.findByReservationCodeAndUserId(reservation.getReservationCode(), reservation.getId())).thenReturn(Optional.of(reservation));

        ReservationInfoDto reservationInfoDto = reservationService.getReservationByCode(reservation.getId(), reservation.getReservationCode());

        assertNotNull(reservationInfoDto);
        assertEquals(reservation.getSeat().getFlight().getAirline(), reservationInfoDto.getAirline());
        assertEquals(reservation.getSeat().getFlight().getFlightCode(), reservationInfoDto.getFlightCode());
        assertEquals(reservation.getSeat().getFlight().getDeparture(), reservationInfoDto.getDeparture());
        assertEquals(reservation.getSeat().getFlight().getArrival(), reservationInfoDto.getArrival());
        assertEquals(reservation.getSeat().getFlight().getOriginAirportCode(), reservationInfoDto.getOriginAirportCode());
        assertEquals(reservation.getSeat().getFlight().getDestinationAirportCode(), reservationInfoDto.getDestinationAirportCode());
        assertEquals(reservation.getSeat().getRow(), reservationInfoDto.getRow());
        assertEquals(reservation.getSeat().getCol(), reservationInfoDto.getCol());
        assertEquals(reservation.getSeat().getSeatType(), reservationInfoDto.getSeatType());
        assertEquals(reservation.getReservationCode(), reservationInfoDto.getReservationCode());
        assertEquals(reservation.getReservationStatus(), reservationInfoDto.getReservationStatus());

    }
}