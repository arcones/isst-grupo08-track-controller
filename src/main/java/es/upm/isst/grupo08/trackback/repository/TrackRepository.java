package es.upm.isst.grupo08.trackback.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upm.isst.grupo08.trackback.model.Track;

public interface TrackRepository extends CrudRepository<Track, String> {

    List<Track> findByTutor(String tutor);

}
