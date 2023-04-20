package com.fligth_booking.fligth_booking_backend.users;

import com.fligth_booking.fligth_booking_backend.exceptions.UserIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.users.userDTOs.UserBasicInfoDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    public List<UserBasicInfoDTO> allActiveUsers(){
        List<UserModel> entities = userRepository.findAllByStatus(Boolean.TRUE);
        if (entities.isEmpty()){
            return new ArrayList<>();
        }
        List<UserBasicInfoDTO> userBasicInfoDTOList = new ArrayList<>();
        for (UserModel user: entities
             ) {
            userBasicInfoDTOList.add(modelMapper.map(user, UserBasicInfoDTO.class));
        }
        return userBasicInfoDTOList;
    }

    public UserBasicInfoDTO getActiveUserById(Long id) throws UserIdNotFoundException {
        Optional<UserModel> userOptional = userRepository.findByIdAndStatus(id, Boolean.TRUE);
        if(userOptional.isEmpty()){
            throw new UserIdNotFoundException(String.format("User Id %s not Found", id));
        }
        return modelMapper.map(userOptional.get(), UserBasicInfoDTO.class);
    }

    public URI createUser(UserModel userModel){
        userModel.setStatus(Boolean.TRUE);
        userRepository.save(userModel);
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userModel.getId())
                .toUri();
    }

    public void updateUser(UserModel userModel) throws UserIdNotFoundException {
        Optional<UserModel> response = userRepository.findById(userModel.getId());
        if(response.isEmpty()){
            throw new UserIdNotFoundException(String.format("User Id %s not Found", userModel.getId()));
        }
        userRepository.save(userModel);
    }

    public void deleteUser(Long id) throws UserIdNotFoundException {
        Optional<UserModel> responseOptional = userRepository.findById(id);
        if(responseOptional.isEmpty()){
            throw new UserIdNotFoundException(String.format("User Id %s not Found", id));
        }
        UserModel userModel = responseOptional.get();
        //TODO:It might be a good idea to check if the user has active bookings before deactivate it
        userModel.setStatus(Boolean.FALSE);
        userRepository.save(userModel);
    }
}
