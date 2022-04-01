package es.upm.isst.grupo08.trackback.repository;

import es.upm.isst.grupo08.trackback.model.Carrier;
import org.springframework.data.repository.CrudRepository;

public interface TrackRepository extends CrudRepository<Carrier, String> {
    Carrier findByName(String name);
}
