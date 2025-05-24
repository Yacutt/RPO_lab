package bmstu.iu3.backend.controllers;

import bmstu.iu3.backend.models.Artist;
import bmstu.iu3.backend.models.Country;
import bmstu.iu3.backend.repositories.ArtistRepository;
import bmstu.iu3.backend.repositories.CountryRepository;
import bmstu.iu3.backend.tools.DataValidationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class ArtistController {

    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;

//    @GetMapping("/artists")
//    public List getAllArtists() {
//        return artistRepository.findAll();
//    }

//    @PostMapping("/artists")
//    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) throws Exception {
//        try {
//            Optional<Country> cc = countryRepository.findById(artist.country.id);
//            if (cc.isPresent()) {
//                artist.country = cc.get();
//            }
//            Artist nc = artistRepository.save(artist);
//            return new ResponseEntity<Object>(nc, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @PutMapping("/artists/{id}")
//    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId,
//                                                 @RequestBody Artist artistDetails) {
//        Artist artist = null;
//        Optional<Artist> cc = artistRepository.findById(artistId);
//        if (cc.isPresent()) {
//            artist = cc.get();
//            artist.name = artistDetails.name;
//            artistRepository.save(artist);
//            return ResponseEntity.ok(artist);
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
//        }
//    }

    @GetMapping("/artists")
    public Page<Artist> getAllArtists(@RequestParam("page") int page,
                                      @RequestParam("limit") int limit) {
        return artistRepository.findAll(PageRequest.of(page, limit, Sort.by("name")));
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@Valid @RequestBody Artist artist)
            throws DataValidationException {
        try {
            Artist na = artistRepository.save(artist);
            return new ResponseEntity<>(na, HttpStatus.OK);
        } catch (Exception ex) {
            if (ex.getMessage().contains("artists.name_UNIQUE"))
                throw new DataValidationException("Этот художник уже есть в базе");
            throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity<Artist> getArtist(@PathVariable Long id)
            throws DataValidationException {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Художник не найден"));
        return ResponseEntity.ok(artist);
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(
            @PathVariable Long id,
            @Valid @RequestBody Artist artistDetails) throws DataValidationException {

        try {
            Artist artist = artistRepository.findById(id)
                    .orElseThrow(() -> new DataValidationException("Художник не найден"));

            artist.name = artistDetails.name;
            artist.century = artistDetails.century;

            if (artistDetails.country != null) {
                Country country = countryRepository.findById(artistDetails.country.id)
                        .orElseThrow(() -> new DataValidationException("Страна не найдена"));
                artist.country = country;            }

            return ResponseEntity.ok(artistRepository.save(artist));

        } catch (DataIntegrityViolationException ex) {
            throw new DataValidationException(ex.getMostSpecificCause().getMessage());
        } catch (Exception ex) {
            throw new DataValidationException(ex.getMessage());
        }
    }

    @PostMapping("/deleteartists")
    public ResponseEntity<?> deleteArtists(@Valid @RequestBody List<Artist> artists) throws DataValidationException {
        try {

            artistRepository.deleteAll(artists);

            return ResponseEntity.ok().build();

        } catch (DataIntegrityViolationException ex) {
            throw new DataValidationException("Нельзя удалить художника с привязанными картинами");
        } catch (Exception ex) {
            throw new DataValidationException(ex.getMessage());
        }
    }

//    @DeleteMapping("/artists/{id}")
//    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long artistId) {
//        Optional<Artist> artist = artistRepository.findById(artistId);
//        Map<String, Boolean> resp = new HashMap<>();
//        if (artist.isPresent()) {
//            artistRepository.delete(artist.get());
//            resp.put("deleted", Boolean.TRUE);
//        }
//        else
//            resp.put("deleted", Boolean.FALSE);
//        return ResponseEntity.ok(resp);
//    }
}
