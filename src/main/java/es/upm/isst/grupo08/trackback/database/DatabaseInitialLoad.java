package es.upm.isst.grupo08.trackback.database;

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
class DatabaseInitialLoad implements CommandLineRunner {

    public static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    private final CarrierRepository carrierRepository;
    private final ParcelRepository parcelRepository;

    DatabaseInitialLoad(CarrierRepository carrierRepository, ParcelRepository parcelRepository) {
        this.carrierRepository = carrierRepository;
        this.parcelRepository = parcelRepository;
    }

    @Override
    public void run(String... args) {
        LOGGER.log(INFO, "Preparing database with initial data...");
        loadInitialCarriers();
        //loadInitialParcel();
        LOGGER.log(INFO, "Database prepared");
    }

//    private void loadInitialParcel() {
//        Parcel initialParcel = new Parcel("SEUR1234XYZ", 1, "En tránsito");
//        if (!parcelRepository.findAll().contains(initialParcel)) {
//            parcelRepository.save(initialParcel);
//        }
//    }

    private void loadInitialCarriers() {
        List<Carrier> initialCarriers = List.of(
                new Carrier("seur", "test"),
                new Carrier("mrw", "test"),
                new Carrier("correos", "test")
        );

        if (!carrierRepository.findAll().containsAll(initialCarriers)) {
            carrierRepository.saveAll(initialCarriers);
        }
    }
}
