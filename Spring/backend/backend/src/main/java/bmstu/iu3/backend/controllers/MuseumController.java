package bmstu.iu3.backend.controllers;

import bmstu.iu3.backend.models.Artist;
import bmstu.iu3.backend.models.Country;
import bmstu.iu3.backend.models.Museum;
import bmstu.iu3.backend.models.User;
import bmstu.iu3.backend.repositories.MuseumRepository;
import bmstu.iu3.backend.repositories.UserRepository;
import bmstu.iu3.backend.tools.DataValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class MuseumController {

    @Autowired
    MuseumRepository museumRepository;
    @Autowired
    UserRepository userRepository;

//    @GetMapping("/museums")
//    public List getAllMuseums() {
//        return museumRepository.findAll();
//    }
//
//    @PostMapping("/museums")
//    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) throws Exception {
//        try {
//            Museum nm = museumRepository.save(museum);
//            return new ResponseEntity<Object>(nm, HttpStatus.OK);
//        } catch (Exception ex) {
//            String error;
//            if (ex.getMessage().contains("museums_name_UNIQUE"))
//                error = "museum_already_exists";
//            else
//                error = "undefinederror";
//            Map<String, String> map =  new HashMap<>();
//            map.put("error", error);
//            return new ResponseEntity<Object> (map, HttpStatus.OK);
//        }
//    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
                                             @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                Optional<Museum> mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId,
                                                @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : u.museums) {
                u.removeMuseum(m);
                cnt++;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

//    @PutMapping("/museums/{id}")
//    public ResponseEntity<Museum> updateMuseum(@PathVariable(value = "id") Long museumId,
//                                                 @RequestBody Museum museumDetails) {
//        Museum museum = null;
//        Optional<Museum> cm = museumRepository.findById(museumId);
//        if (cm.isPresent()) {
//            museum = cm.get();
//            museum.name = museumDetails.name;
//            museum.location = museumDetails.location;
//            museumRepository.save(museum);
//            return ResponseEntity.ok(museum);
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "museum not found");
//        }
//    }
//
//    @DeleteMapping("/museums/{id}")
//    public ResponseEntity<Object> deleteMuseum(@PathVariable(value = "id") Long museumId) {
//        Optional<Museum> museum = museumRepository.findById(museumId);
//        Map<String, Boolean> resp = new HashMap<>();
//        if (museum.isPresent()) {
//            museumRepository.delete(museum.get());
//            resp.put("deleted", Boolean.TRUE);
//        }
//        else
//            resp.put("deleted", Boolean.FALSE);
//        return ResponseEntity.ok(resp);
//
//    }

    @GetMapping("/museums")
    public Page<Museum> getAllMuseums(@RequestParam("page") int page,
                                      @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by("name")));
    }

    @PostMapping("/museums")
    public ResponseEntity<Museum> createMuseum(@Valid @RequestBody Museum museum)
            throws DataValidationException {
        try {
            return ResponseEntity.ok(museumRepository.save(museum));
        } catch (Exception ex) {
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                throw new DataValidationException("Музей с таким названием уже существует");
            throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @GetMapping("/museums/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable Long id)
            throws DataValidationException {
        Museum museum = museumRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Музей не найден"));
        return ResponseEntity.ok(museum);
    }

    @PutMapping("/museums/{id}")
    public ResponseEntity<Museum> updateMuseum(@PathVariable Long id,
                                               @Valid @RequestBody Museum museumDetails)
            throws DataValidationException {
        try {
            Museum museum = museumRepository.findById(id)
                    .orElseThrow(() -> new DataValidationException("Музей не найден"));
            museum.name = museumDetails.name;
            museum.location = museumDetails.location;

            return ResponseEntity.ok(museumRepository.save(museum));
        } catch (Exception ex) {
            if (ex.getMessage().contains("museums.name_UNIQUE"))
                throw new DataValidationException("Музей с таким названием уже существует");
            throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/deletemuseums")
    public ResponseEntity deleteMuseums(@Valid @RequestBody List<Museum> museums) {
        museumRepository.deleteAll(museums);
        return ResponseEntity.ok().build();
    }

}
