package es.upm.isst.grupo08.trackback.util;

import es.upm.isst.grupo08.trackback.controller.TrackController;
import es.upm.isst.grupo08.trackback.model.Carrier;
import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.CarrierRepository;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

@Component
class PrepareDatabase implements CommandLineRunner {

    public static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    private final CarrierRepository carrierRepository;
    private final ParcelRepository parcelRepository;

    PrepareDatabase(CarrierRepository carrierRepository, ParcelRepository parcelRepository) {
        this.carrierRepository = carrierRepository;
        this.parcelRepository = parcelRepository;
    }

    @Override
    public void run(String... args) {
        if (carrierRepository.findAll().isEmpty()) {
            LOGGER.log(INFO, "Filling database with carriers...");

            List<Carrier> initialCarriers = List.of(
                    new Carrier("seur", "test"),
                    new Carrier("mrw", "test"),
                    new Carrier("correos", "test")
            );

            carrierRepository.saveAll(initialCarriers);
        }
        if (parcelRepository.findAll().isEmpty()) {
            LOGGER.log(INFO, "Filling database with parcels...");
            parcelRepository.save(new Parcel("SEUR1234XYZ", 1, "En tránsito"));
        }
        LOGGER.log(INFO, "Database prepared");
    }



}
