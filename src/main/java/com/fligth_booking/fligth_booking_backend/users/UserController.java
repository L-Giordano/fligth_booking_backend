package com.fligth_booking.fligth_booking_backend.users;

import com.fligth_booking.fligth_booking_backend.exceptions.UserIdNotFoundException;
import com.fligth_booking.fligth_booking_backend.users.userDTOs.UserBasicInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        try {
            List<UserBasicInfoDTO> response = userService.allActiveUsers();
            if (response.isEmpty()){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        try {
            UserBasicInfoDTO userResponse = userService.getActiveUserById(id);
            return ResponseEntity.ok(userResponse);
        }catch (UserIdNotFoundException e){
                return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserModel userModel){
        try {
            URI uri = userService.createUser(userModel);
            return ResponseEntity.created(uri).build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<Object> updateUser(@RequestBody UserModel userModel){
        try {
            userService.updateUser(userModel);
            return ResponseEntity.noContent().build();
        }catch (UserIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id){
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }catch (UserIdNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
