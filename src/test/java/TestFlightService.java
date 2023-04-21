import com.fligth_booking.fligth_booking_backend.exceptions.FlightIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.exceptions.InvalidSeatKeyPatternException;
import com.fligth_booking.fligth_booking_backend.flights.FlightModel;
import com.fligth_booking.fligth_booking_backend.flights.FlightRepository;
import com.fligth_booking.fligth_booking_backend.flights.FlightService;
import com.fligth_booking.fligth_booking_backend.flights.flightDTOs.FlightBasicInfoDTO;
import com.fligth_booking.fligth_booking_backend.seats.SeatModel;
import com.fligth_booking.fligth_booking_backend.seats.SeatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@RunWith(MockitoJUnitRunner.class)
public class TestFlightService {

    @Spy
    @InjectMocks
    FlightService flightService;

    @Mock
    FlightRepository flightRepository;

    @Mock
    SeatService seatService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeClass
    public static void setupDateNow(){
        String instantExpected = String.valueOf(LocalDate.of(2023, 5, 8));
        LocalDate localDate = LocalDate.parse(instantExpected);
        MockedStatic<LocalDate> mockedLocalDateTime = Mockito.mockStatic(LocalDate.class);
        mockedLocalDateTime.when(LocalDate::now).thenReturn(localDate);
    }

    public FlightModel setupFlightModelToTest(Long id){

        FlightModel flightTest = new FlightModel();
        flightTest.setId(id);
        flightTest.setAirline("airline");
        flightTest.setDeparture(ZonedDateTime.of(2023, 04, 18, 10, 0, 0, 0, ZoneId.of("UTC")));
        flightTest.setArrival(ZonedDateTime.of(2023, 04, 18, 12, 0, 0, 0, ZoneId.of("UTC")));
        flightTest.setOriginAirportCode("MDZ");
        flightTest.setDestinationAirportCode("AEP");
        return flightTest;
    }

    @Test
    public void testWhenNoExistFlightsReturnEmpty() {

        List<FlightModel> entities = new ArrayList<>();
        when(flightRepository.findAllByStatusAndDepartureDateIsGreaterThan(true, LocalDate.now())).thenReturn(entities);
        List<FlightBasicInfoDTO> result = flightService.allActiveFlights();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetFlightsWhenExitsFlights(){

        List<FlightModel> mockedFlights = new ArrayList<>();

        FlightModel flight1 = this.setupFlightModelToTest(1L);
        flight1.setStatus(true);
        mockedFlights.add(flight1);
        FlightModel flight2 = this.setupFlightModelToTest(2L);
        flight2.setStatus(true);
        mockedFlights.add(flight2);

        when(flightRepository.findAllByStatusAndDepartureDateIsGreaterThan(true, LocalDate.now())).thenReturn(mockedFlights);

        List<FlightBasicInfoDTO> flightBasicInfoDTOList = flightService.allActiveFlights();

        assertEquals(2, flightBasicInfoDTOList.size());
    }

    @Test(expected = FlightIdNotFoundException.class)
    public void testGetActiveFlightByIdWithInvalidId() throws FlightIdNotFoundException {
        when(flightRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.empty());
        flightService.getActiveFlightById(1L);
    }

    @Test
    public void testGetActiveFlightById() throws FlightIdNotFoundException {
        FlightModel flight = this.setupFlightModelToTest(1l);
        flight.setStatus(true);

        Optional<FlightModel> flightOptional = Optional.of(flight);

        when(flightRepository.findByIdAndStatus(1L, true)).thenReturn(flightOptional);
        flightService.getActiveFlightById(flight.getId());
        verify(modelMapper).map(flight, FlightBasicInfoDTO.class);
    }

    @Test
    public void testCreateFlightReturnCorrectUri() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/flights");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        FlightModel flight = this.setupFlightModelToTest(1l);

        URI uri = flightService.createFlight(flight);

        URI expectedUri = URI.create("http://localhost/flights/" + flight.getId());
        assertEquals(expectedUri, uri);
    }

    @Test
    public void testCreateFlightSetFlightCorrectly(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Mockito.doReturn("AIRLINE-AXFRG").when(flightService).createFlightCode("airline");

        FlightModel flight = this.setupFlightModelToTest(1l);
        flightService.createFlight(flight);

        assertTrue(flight.getStatus());
        assertEquals(LocalDate.of(2023, 04, 18), flight.getDepartureDate());
        assertEquals(LocalDate.of(2023, 04, 18), flight.getArrivalDate());
        assertEquals("AIRLINE-AXFRG", flight.getFlightCode());
    }

    @Test
    public void createFlightCode(){
        String flightCode = flightService.createFlightCode("airline");
        assertTrue(flightCode.matches("AIRLINE-[A-Z]{5}"));
    }

    @Test
    public void testAllFlightsByDepartureArrival() {
        Integer pageNum = 0;
        Integer pageSize = 10;
        Boolean status = true;
        String originAirportCode = "MDZ";
        String destinationAirportCode = "AEP";
        LocalDate departure = LocalDate.of(2023,04,18);
        LocalDate arrival = LocalDate.of(2023,04,18);

        List<FlightModel> flightList = Arrays.asList(
                this.setupFlightModelToTest(1L),
                this.setupFlightModelToTest(1L),
                this.setupFlightModelToTest(1L)
        );

        Page<FlightModel> expectedPage = new PageImpl<>(flightList);

        when(flightRepository.findFlightsByDepartureArrivalOriginDestination(
                originAirportCode,
                destinationAirportCode,
                departure,
                arrival,
                status,
                PageRequest.of(pageNum, pageSize)
        )).thenReturn(expectedPage);


        Page<FlightModel> resultPage = flightService.allFlightsByDepartureArrival(
                pageNum,
                pageSize,
                originAirportCode,
                destinationAirportCode,
                departure,
                arrival,
                status
        );

        assertEquals(expectedPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(expectedPage.getNumber(), resultPage.getNumber());
        assertEquals(expectedPage.getSize(), resultPage.getSize());
        assertEquals(expectedPage.getContent().size(), resultPage.getContent().size());
        assertEquals(expectedPage.getContent().get(0).getFlightCode(), resultPage.getContent().get(0).getFlightCode());
        assertEquals(expectedPage.getContent().get(1).getFlightCode(), resultPage.getContent().get(1).getFlightCode());
        assertEquals(expectedPage.getContent().get(2).getFlightCode(), resultPage.getContent().get(2).getFlightCode());
    }

    @Test
    public void testCreateSeats() throws FlightIdNotFoundException, InvalidSeatKeyPatternException {

        FlightModel flight = this.setupFlightModelToTest(1L);
        HashMap<String, ArrayList<String>> seats = new HashMap<>();
        ArrayList<String> seatInfo = new ArrayList<>(Arrays.asList("ECONOMIC", "100.00"));
        seats.put("A1", seatInfo);

        when(flightRepository.findById(flight.getId())).thenReturn(Optional.of(flight));

        flightService.createSeats(flight.getId(), seats);

        verify(seatService, times(1)).createSeat(any(SeatModel.class));
    }

    @Test
    public void testAllSeats() throws FlightIdNotFoundException {

        FlightModel flight = this.setupFlightModelToTest(1L);
        SeatModel seat1 = new SeatModel();
        seat1.setCol("A");
        seat1.setRow("1");
        SeatModel seat2 = new SeatModel();
        seat2.setCol("B");
        seat2.setRow("1");
        List<SeatModel> seats = new ArrayList<>();
        seats.add(seat1);
        seats.add(seat2);
        flight.setSeats(seats);

        when(flightRepository.findById(flight.getId())).thenReturn(Optional.of(flight));

        List<SeatModel> result = flightService.allSeats(flight.getId());

        assertEquals(seats, result);
    }

    @Test(expected = FlightIdNotFoundException.class)
    public void testAllSeatsThrowsException() throws FlightIdNotFoundException {
        Long id = 1L;
        when(flightRepository.findById(id)).thenReturn(Optional.empty());
        flightService.allSeats(id);
    }

    @Test(expected = InvalidSeatKeyPatternException.class)
    public void testCreateSeatsInvalidKey() throws FlightIdNotFoundException, InvalidSeatKeyPatternException {
        HashMap<String, ArrayList<String>> seats = new HashMap<String, ArrayList<String>>();
        ArrayList<String> seatInfo = new ArrayList<>(Arrays.asList("ECONOMIC", "100.00"));
        seats.put("INVALID_KEY", seatInfo);
        FlightModel flight = this.setupFlightModelToTest(1L);

        when(flightRepository.findById(flight.getId())).thenReturn(Optional.of(flight));

        flightService.createSeats(flight.getId(), seats);

    }

    @Test(expected = FlightIdNotFoundException.class)
    public void testUpdateFlightWithInvalidId() throws FlightIdNotFoundException {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
        FlightModel flight = this.setupFlightModelToTest(1l);
        flightService.updateFlight(flight);
    }

    @Test
    public void testUpdateFlightWithValidId() throws FlightIdNotFoundException {
        FlightModel flight = this.setupFlightModelToTest(1l);
        Optional<FlightModel> flightOptional = Optional.of(flight);

        when(flightRepository.findById(1L)).thenReturn(flightOptional);
        flightService.updateFlight(flight);

        Mockito.verify(flightRepository, Mockito.times(1)).save(flightOptional.get());
    }

    @Test
    public void testDeleteFlightChangeStatusToFalse() throws FlightIdNotFoundException {
        FlightModel flight = this.setupFlightModelToTest(1l);

        Optional<FlightModel> flightOptional = Optional.of(flight);

        when(flightRepository.findById(1L)).thenReturn(flightOptional);
        flightService.deleteFlight(flight.getId());

        assertFalse(flight.getStatus());
    }

    @Test(expected = FlightIdNotFoundException.class)
    public void testDeleteUserWithInvalidId() throws FlightIdNotFoundException {
        when(flightRepository.findById(1L)).thenReturn(Optional.empty());
        flightService.deleteFlight(1L);
    }

    @Test
    public void testIsValidateKeyPattern() {
        assertTrue(flightService.isValidateKeyPattern("ABC-123-DEF"));
        assertTrue(flightService.isValidateKeyPattern("A-1-BC"));
        assertFalse(flightService.isValidateKeyPattern("ABC--DEF"));
        assertFalse(flightService.isValidateKeyPattern("12-123-def"));
        assertFalse(flightService.isValidateKeyPattern("A-BC-123"));
        assertFalse(flightService.isValidateKeyPattern("AB-1C-DE"));
    }
}
