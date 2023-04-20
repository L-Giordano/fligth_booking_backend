import com.fligth_booking.fligth_booking_backend.exceptions.UserIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.users.UserModel;
import com.fligth_booking.fligth_booking_backend.users.UserRepository;
import com.fligth_booking.fligth_booking_backend.users.UserService;
import com.fligth_booking.fligth_booking_backend.users.userDTOs.UserBasicInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestUserService {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;


    public UserModel setupUserModelToTest(Long id){
        UserModel userTest = new UserModel();
        userTest.setId(id);
        userTest.setFirstName("User");
        userTest.setFirstName("User");
        userTest.setEmail(String.format("user%s@user.com", id));
        userTest.setPassword("1234");
        return userTest;
    }

    @Test
    public void testWhenNoExistUsersReturnEmptyList() {
        List<UserModel> entities = new ArrayList<>();
        when(userRepository.findAllByStatus(true)).thenReturn(entities);
        List<UserBasicInfoDTO> result = userService.allActiveUsers();
        assertEquals(0, result.size());
    }

    @Test
    public void testWhenExistsActiveUsers() {
        List<UserModel> mockedUserResult = new ArrayList<>();

        UserModel user1 = this.setupUserModelToTest(1l);
        user1.setStatus(true);
        mockedUserResult.add(user1);

        UserModel user2 = this.setupUserModelToTest(2l);
        user1.setStatus(true);
        mockedUserResult.add(user2);

        when(userRepository.findAllByStatus(true)).thenReturn(mockedUserResult);

        List<UserBasicInfoDTO> result = userService.allActiveUsers();
        assertEquals(2, result.size());
    }

    @Test(expected = UserIdNotFoundException.class)
    public void testGetActiveUserByIdWithInvalidId() throws UserIdNotFoundException {
        when(userRepository.findByIdAndStatus(1L, true)).thenReturn(Optional.empty());
        userService.getActiveUserById(1L);
    }

    @Test
    public void testGetActiveUserById() throws UserIdNotFoundException {
        UserModel user1 = this.setupUserModelToTest(1l);
        user1.setStatus(true);

        Optional<UserModel> userOptional = Optional.of(user1);

        when(userRepository.findByIdAndStatus(1L, true)).thenReturn(userOptional);
        userService.getActiveUserById(user1.getId());
        Mockito.verify(modelMapper).map(user1, UserBasicInfoDTO.class);
    }

    @Test
    public void testCreateUserReturnCorrectUri() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/users");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserModel user1 = this.setupUserModelToTest(1l);

        URI uri = userService.createUser(user1);

        URI expectedUri = URI.create("http://localhost/users/" + user1.getId());
        assertEquals(expectedUri, uri);
    }

    @Test
    public void testCreateUserSetUserStatusTrue(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        UserModel user1 = this.setupUserModelToTest(1l);
        userService.createUser(user1);
        assertTrue(user1.getStatus());
    }

    @Test(expected = UserIdNotFoundException.class)
    public void testUpdateUserWithInvalidId() throws UserIdNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserModel user1 = this.setupUserModelToTest(1l);
        user1.setStatus(true);
        userService.updateUser(user1);
    }

    @Test
    public void testUpdateUserWithValidId() throws UserIdNotFoundException {
        UserModel user1 = this.setupUserModelToTest(1l);
        Optional<UserModel> userOptional = Optional.of(user1);

        when(userRepository.findById(1L)).thenReturn(userOptional);
        userService.updateUser(user1);

        Mockito.verify(userRepository, Mockito.times(1)).save(userOptional.get());
    }

    @Test
    public void testDeleteUserChangeStatusToFalse() throws UserIdNotFoundException {
        UserModel user1 = this.setupUserModelToTest(1l);
        user1.setStatus(true);

        Optional<UserModel> userOptional = Optional.of(user1);

        when(userRepository.findById(1L)).thenReturn(userOptional);
        userService.deleteUser(user1.getId());

        assertFalse(user1.getStatus());
    }

    @Test(expected = UserIdNotFoundException.class)
    public void testDeleteUserWithInvalidId() throws UserIdNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserModel user1 = this.setupUserModelToTest(1l);
        user1.setStatus(true);
        userService.deleteUser(user1.getId());
    }
}
