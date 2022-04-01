package es.upm.isst.grupo08.trackback.controller;

import es.upm.isst.grupo08.trackback.model.Carrier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.upm.isst.grupo08.trackback.repository.TrackRepository;

import java.util.List;

@RestController
public class TrackController {

    private final TrackRepository trackRepository;

    public TrackController(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    @GetMapping("/carriers/{id}")
    public ResponseEntity<Carrier> getById(@RequestParam long id) {
        try {
            //System.out.println("CLASE" + id.getClass());

            //long newId = Long.parseLong(id);
            Carrier carrier = trackRepository.findById(id);
            return new ResponseEntity<>(carrier, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}