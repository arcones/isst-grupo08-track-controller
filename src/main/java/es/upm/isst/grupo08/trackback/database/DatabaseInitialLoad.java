package es.upm.isst.grupo08.trackback.database;

import es.upm.isst.grupo08.trackback.controller.TrackController;
import es.upm.isst.grupo08.trackback.model.ApplicationUser;
import es.upm.isst.grupo08.trackback.repository.UserRepository;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

import static es.upm.isst.grupo08.trackback.model.Role.CARRIER;
import static es.upm.isst.grupo08.trackback.model.Role.RECIPIENT;
import static java.util.logging.Level.INFO;

@Component
class DatabaseInitialLoad implements CommandLineRunner {

    public static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    private final UserRepository userRepository;

    DatabaseInitialLoad(UserRepository userRepository, ParcelRepository parcelRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        LOGGER.log(INFO, "Preparing database with initial data...");
        loadInitialUsers();
        LOGGER.log(INFO, "Database prepared");
    }

    private void loadInitialUsers() {
        List<ApplicationUser> initialApplicationUsers = List.of(
                new ApplicationUser("seur", "test", CARRIER),
                new ApplicationUser("mrw", "test", CARRIER),
                new ApplicationUser("correos", "test", CARRIER),
                new ApplicationUser("pepa", "test", RECIPIENT)
        );

        if (!userRepository.findAll().containsAll(initialApplicationUsers)) {
            userRepository.saveAll(initialApplicationUsers);
        }
    }
}
