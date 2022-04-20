package es.upm.isst.grupo08.trackback.util;

import es.upm.isst.grupo08.trackback.controller.TrackController;
import es.upm.isst.grupo08.trackback.model.Carrier;
import es.upm.isst.grupo08.trackback.repository.CarrierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

@Component
class PrepareDatabase implements CommandLineRunner {

    public static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    private final CarrierRepository carrierRepository;

    PrepareDatabase(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    @Override
    public void run(String... args) {

        LOGGER.log(INFO, "Filling database...");

        List<Carrier> initialCarriers = List.of(
            new Carrier("seur", "test"),
            new Carrier("mrw", "test"),
            new Carrier("correos", "test")
        );

        carrierRepository.saveAll(initialCarriers);

        LOGGER.log(INFO, "Database prepared");

    }

}
