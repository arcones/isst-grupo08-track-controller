package es.upm.isst.grupo08.trackback.repository;

import es.upm.isst.grupo08.trackback.model.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CarrierRepository extends JpaRepository<Carrier, Long> {
}
