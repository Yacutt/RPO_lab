package bmstu.iu3.backend.controllers;

import bmstu.iu3.backend.models.Artist;
import bmstu.iu3.backend.models.Country;
import bmstu.iu3.backend.repositories.CountryRepository;
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
public class CountryController {
    @Autowired
    CountryRepository countryRepository;

//    @GetMapping("/countries")
//    public List<Country> getAllCountries() {
//        return countryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
//    }

    @GetMapping("/countries")
    public Page<Country> getAllCountries(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return countryRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/countries/{id}/artists")
    public ResponseEntity<List<Artist>> getCountryArtists(@PathVariable(value = "id") Long countryId) {
        Optional<Country> cc = countryRepository.findById(countryId);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().artists);
        }
        return ResponseEntity.ok(new ArrayList<Artist>());
    }

//    @PostMapping("/countries")
//    public ResponseEntity<Object> createCountry(@RequestBody Country country) throws Exception {
//        try {
//            Country nc = countryRepository.save(country);
//            return new ResponseEntity<Object>(nc, HttpStatus.OK);
//        }
//        catch(Exception ex) {
//            String error;
//            if (ex.getMessage().contains("countries_name_UNIQUE"))
//                error = "countyalreadyexists";
//            else
//                error = "undefinederror";
//            Map<String, String> map =  new HashMap<>();
//            map.put("error", error);
//            return new ResponseEntity<Object> (map, HttpStatus.OK);
//        }
//    }

    @PostMapping("/countries")
    public ResponseEntity<Object> createCountry(@Valid @RequestBody Country country)
            throws DataValidationException {
        try {
            Country nc = countryRepository.save(country);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("countries.name_UNIQUE"))
                throw new DataValidationException("Эта страна уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

//    @PutMapping("/countries/{id}")
//    public ResponseEntity<Country> updateCountry(@PathVariable(value = "id") Long countryId,
//                                                 @RequestBody Country countryDetails) {
//        Country country = null;
//        Optional<Country> cc = countryRepository.findById(countryId);
//        if (cc.isPresent()) {
//            country = cc.get();
//            country.name = countryDetails.name;
//            countryRepository.save(country);
//            return ResponseEntity.ok(country);
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "country not found");
//        }
//    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Object> deleteCountry(@PathVariable(value = "id") Long countryId) {
        Optional<Country> country = countryRepository.findById(countryId);
        Map<String, Boolean> resp = new HashMap<>();
        if (country.isPresent()) {
            countryRepository.delete(country.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity getCountry(@PathVariable(value = "id") Long countryId) throws DataValidationException {
        Country country = countryRepository.findById(countryId)
                .orElseThrow(()->new DataValidationException("Страна с таким индексом не найдена"));
        return ResponseEntity.ok(country);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable(value = "id") Long countryId,
                                                 @Valid @RequestBody Country countryDetails) throws DataValidationException {
        try {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));
            country.name = countryDetails.name;
            countryRepository.save(country);
            return ResponseEntity.ok(country);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("countries.name_UNIQUE"))
                throw new DataValidationException("Эта страна уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @PostMapping("/deletecountries")
    public ResponseEntity deleteCountries(@Valid @RequestBody List<Country> countries) {
        countryRepository.deleteAll(countries);
        return new ResponseEntity(HttpStatus.OK);
    }

}