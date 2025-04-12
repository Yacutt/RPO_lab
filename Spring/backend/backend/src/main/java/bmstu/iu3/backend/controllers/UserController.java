package bmstu.iu3.backend.controllers;

import bmstu.iu3.backend.models.Country;
import bmstu.iu3.backend.models.User;
import bmstu.iu3.backend.models.Views;
import bmstu.iu3.backend.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @JsonView(Views.Public.class)
    @GetMapping("/users")
    public List getAllUsers() {
        return userRepository.findAll();
    }

    @JsonView(Views.Public.class)
    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User user) throws Exception {
        try {
            User nu = userRepository.save(user);
            return new ResponseEntity<Object>(nu, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("users_email_UNIQUE"))
                error = "user_already_exists";
            else
                error = "undefined_error";
            Map<String, String> map =  new HashMap<>();
            map.put("error", error);
            return new ResponseEntity<Object> (map, HttpStatus.OK);
        }
    }

    @JsonView(Views.Public.class)
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
                                           @RequestBody User userDetails) {
        User user = null;
        Optional uu = userRepository.findById(userId);
        if (uu.isPresent()) {
            user = (User) uu.get();
            user.login = userDetails.login;
            user.email = userDetails.email;
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
        }
    }

    @JsonView(Views.Public.class)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
        Optional<User> user = userRepository.findById(userId);
        Map<String, Boolean> resp = new HashMap<>();
        if (user.isPresent()) {
            userRepository.delete(user.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

}
