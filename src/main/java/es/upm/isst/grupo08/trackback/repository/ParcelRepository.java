package es.upm.isst.grupo08.trackback.repository;

import es.upm.isst.grupo08.trackback.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
}
