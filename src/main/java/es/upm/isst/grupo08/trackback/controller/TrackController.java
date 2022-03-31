package es.upm.isst.grupo08.trackback.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.upm.isst.grupo08.trackback.model.Track;
import es.upm.isst.grupo08.trackback.repository.TrackRepository;

@RestController
public class TrackController {

    private final TrackRepository trackRepository;

    public static final Logger log = LoggerFactory.getLogger(TrackController.class);

    public TrackController(TrackRepository t) {

        this.trackRepository = t;

    }

    @GetMapping("/tracks")

    List<Track> readAll() {

      return (List<Track>) trackRepository.findAll();

    }

 

    @PostMapping("/tracks")

    ResponseEntity<Track> create(@RequestBody Track newTrack) throws URISyntaxException {

      Track result = trackRepository.save(newTrack);

      return ResponseEntity.created(new URI("/tracks/" + result.getNombre())).body(result); 
      
      //------------------------ getNombre inventado.----------------------------------------

    }
    

 

    @GetMapping("/tracks/{id}")

    ResponseEntity<Track> read(@PathVariable String id) {

      return trackRepository.findById(id).map(track ->

         ResponseEntity.ok().body(track)

      ).orElse(new ResponseEntity<Track>(HttpStatus.NOT_FOUND));

    }


    @PutMapping("/tracks/{id}")

    ResponseEntity<Track> update(@RequestBody Track newTrack, @PathVariable String id) {

      return trackRepository.findById(id).map(track -> {

        track.setNombre(newTrack.getNombre());



        track.setStatus(newTrack.getStatus());



        trackRepository.save(track);

        return ResponseEntity.ok().body(track);

      }).orElse(new ResponseEntity<Track>(HttpStatus.NOT_FOUND));

    }


    @DeleteMapping("tracks/{id}")

    ResponseEntity<Track> delete(@PathVariable String id) {

      trackRepository.deleteById(id);

      return ResponseEntity.ok().body(null);

    }


    @GetMapping("/tfgs/profesor/{id}")

    List<Track> readTutor(@PathVariable String id) {

      return (List<Track>) trackRepository.findByTutor(id);

    }


    @PostMapping("/tracks/{id}/incrementa")

    ResponseEntity<Track> incrementa(@PathVariable String id) {

      return trackRepository.findById(id).map(track -> {

       track.setStatus(track.getStatus() + 1);

        trackRepository.save(track);

        return ResponseEntity.ok().body(track);

      }).orElse(new ResponseEntity<Track>(HttpStatus.NOT_FOUND));  

    }

}