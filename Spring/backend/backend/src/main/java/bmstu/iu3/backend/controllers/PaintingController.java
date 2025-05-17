package bmstu.iu3.backend.controllers;

import bmstu.iu3.backend.models.Artist;
import bmstu.iu3.backend.models.Country;
import bmstu.iu3.backend.models.Museum;
import bmstu.iu3.backend.models.Painting;
import bmstu.iu3.backend.repositories.ArtistRepository;
import bmstu.iu3.backend.repositories.MuseumRepository;
import bmstu.iu3.backend.repositories.PaintingRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    MuseumRepository museumRepository;

//    @GetMapping("/paintings")
//    public List getAllPaintings() {
//        return paintingRepository.findAll();
//    }
//
//    @PostMapping("/paintings")
//    public ResponseEntity<Object> createPainting(@RequestBody Painting painting) throws Exception {
//        try {
//            Optional<Artist> artist = artistRepository.findById(painting.artist.id);
//            Optional<Museum> museum = museumRepository.findById(painting.museum.id);
//            artist.ifPresent(value -> painting.artist = value);
//            museum.ifPresent(value -> painting.museum = value);
//
//            Painting np = paintingRepository.save(painting);
//            return new ResponseEntity<Object>(np, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @PutMapping("/paintings/{id}")
//    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId,
//                                               @RequestBody Painting paintingDetails) {
//        Painting painting = null;
//        Optional<Painting> pp = paintingRepository.findById(paintingId);
//        if (pp.isPresent()) {
//            painting = pp.get();
//            painting.name = paintingDetails.name;
//            painting.year = paintingDetails.year;
//            paintingRepository.save(painting);
//            return ResponseEntity.ok(painting);
//        } else {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "painting not found");
//        }
//    }
//
//    @DeleteMapping("/paintings/{id}")
//    public ResponseEntity<Object> deletePainting(@PathVariable(value = "id") Long paintingId) {
//        Optional<Painting> painting = paintingRepository.findById(paintingId);
//        Map<String, Boolean> resp = new HashMap<>();
//        if (painting.isPresent()) {
//            paintingRepository.delete(painting.get());
//            resp.put("deleted", Boolean.TRUE);
//        }
//        else
//            resp.put("deleted", Boolean.FALSE);
//        return ResponseEntity.ok(resp);
//    }

    @GetMapping("/paintings")
    public Page<Painting> getAllPaintings(@RequestParam("page") int page,
                                          @RequestParam("limit") int limit) {
        return paintingRepository.findAll(PageRequest.of(page, limit, Sort.by("name")));
    }

    @PostMapping("/paintings")
    public ResponseEntity<Painting> createPainting(@Valid @RequestBody Painting painting)
            throws DataValidationException {
        try {
            painting.artist = artistRepository.findById(painting.artist.id)
                    .orElseThrow(() -> new DataValidationException("Художник не найден"));
            return ResponseEntity.ok(paintingRepository.save(painting));
        } catch (Exception ex) {
            throw new DataValidationException("Ошибка при создании картины");
        }
    }

    @GetMapping("/paintings/{id}")
    public ResponseEntity<Painting> getPainting(@PathVariable Long id)
            throws DataValidationException {
        Painting painting = paintingRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Картина не найдена"));
        return ResponseEntity.ok(painting);
    }

    @PutMapping("/paintings/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable Long id,
                                                   @Valid @RequestBody Painting paintingDetails)
            throws DataValidationException {
        try {
            Painting painting = paintingRepository.findById(id)
                    .orElseThrow(() -> new DataValidationException("Картина не найдена"));
            painting.name = paintingDetails.name;

            painting.artist = artistRepository.findById(paintingDetails.artist.id)
                    .orElseThrow(() -> new DataValidationException("Художник не найден"));

            painting.museum = museumRepository.findById(paintingDetails.museum.id)
                    .orElseThrow(() -> new DataValidationException("Музей не найден"));

            return ResponseEntity.ok(paintingRepository.save(painting));
        } catch (Exception ex) {
            throw new DataValidationException("Ошибка при обновлении картины");
        }
    }

    @PostMapping("/deletepaintings")
    public ResponseEntity deletePaintings(@Valid @RequestBody List<Painting> paintings) {
        paintingRepository.deleteAll(paintings);
        return ResponseEntity.ok().build();
    }

}
