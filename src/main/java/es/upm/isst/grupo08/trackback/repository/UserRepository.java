package es.upm.isst.grupo08.trackback.repository;

import es.upm.isst.grupo08.trackback.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
}
