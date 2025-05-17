package bmstu.iu3.backend.controllers;

import bmstu.iu3.backend.models.Country;
import bmstu.iu3.backend.models.User;
import bmstu.iu3.backend.models.Views;
import bmstu.iu3.backend.repositories.UserRepository;
import bmstu.iu3.backend.tools.DataValidationException;
import bmstu.iu3.backend.tools.Utils;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    UserRepository userRepository;

//    @JsonView(Views.Public.class)
//    @GetMapping("/users")
//    public List getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @JsonView(Views.Public.class)
//    @PostMapping("/users")
//    public ResponseEntity<Object> createUser(@RequestBody User user) throws Exception {
//        try {
//            User nu = userRepository.save(user);
//            return new ResponseEntity<Object>(nu, HttpStatus.OK);
//        }
//        catch(Exception ex) {
//            String error;
//            if (ex.getMessage().contains("users_email_UNIQUE"))
//                error = "user_already_exists";
//            else
//                error = "undefined_error";
//            Map<String, String> map =  new HashMap<>();
//            map.put("error", error);
//            return new ResponseEntity<Object> (map, HttpStatus.OK);
//        }
//    }
//
//    @JsonView(Views.Public.class)
//    @PutMapping("/users/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable(value = "id") Long userId,
//                                           @RequestBody User userDetails) {
//        User user = null;
//        Optional uu = userRepository.findById(userId);
//        if (uu.isPresent()) {
//            user = (User) uu.get();
//            user.login = userDetails.login;
//            user.email = userDetails.email;
//            userRepository.save(user);
//            return ResponseEntity.ok(user);
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
//        }
//    }
//
//    @JsonView(Views.Public.class)
//    @DeleteMapping("/users/{id}")
//    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
//        Optional<User> user = userRepository.findById(userId);
//        Map<String, Boolean> resp = new HashMap<>();
//        if (user.isPresent()) {
//            userRepository.delete(user.get());
//            resp.put("deleted", Boolean.TRUE);
//        }
//        else
//            resp.put("deleted", Boolean.FALSE);
//        return ResponseEntity.ok(resp);
//    }

    @GetMapping("/users")
    public Page<User> getAllUsers(@RequestParam("page") int page,
                                  @RequestParam("limit") int limit) {
        return userRepository.findAll(PageRequest.of(page, limit, Sort.by("login")));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user)
            throws DataValidationException {
        try {
            return ResponseEntity.ok(userRepository.save(user));
        } catch (Exception ex) {
            if (ex.getMessage().contains("users.login_UNIQUE"))
                throw new DataValidationException("Пользователь с таким логином уже существует");
            throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id)
            throws DataValidationException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Пользователь не найден"));
        return ResponseEntity.ok(user);
    }

//    @PutMapping("/users/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id,
//                                           @Valid @RequestBody User userDetails)
//            throws DataValidationException {
//        try {
//            User user = userRepository.findById(id)
//                    .orElseThrow(() -> new DataValidationException("Пользователь не найден"));
//            user.login = userDetails.login;
//            user.email = userDetails.email;
//            return ResponseEntity.ok(userRepository.save(user));
//        } catch (Exception ex) {
//            if (ex.getMessage().contains("users.login_UNIQUE"))
//                throw new DataValidationException("Пользователь с таким логином уже существует");
//            throw new DataValidationException("Неизвестная ошибка");
//        }
//    }

    @PutMapping("/users/{id}")
    public ResponseEntity updateUser(@PathVariable(value = "id") Long userId,
                                     @Valid @RequestBody User userDetails)
            throws DataValidationException
    {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException(" Пользователь с таким индексом не найден"));
            user.email = userDetails.email;
            user.login = userDetails.login;
            String np = userDetails.np;
            if (np != null  && !np.isEmpty()) {
                byte[] b = new byte[32];
                new Random().nextBytes(b);
                String salt = new String(Hex.encode(b));
                user.password = Utils.ComputeHash(np, salt);
                user.salt = salt;
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.email_UNIQUE"))
                throw new DataValidationException("Пользователь с такой почтой уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/deleteusers")
    public ResponseEntity deleteUsers(@Valid @RequestBody List<User> users) {
        userRepository.deleteAll(users);
        return ResponseEntity.ok().build();
    }
}
