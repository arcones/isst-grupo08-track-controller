package es.upm.isst.grupo08.trackback.controller;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.CarrierRepository;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

@RestController
@Tag(name = "Track", description = "The Trackermaster API")
public class TrackController {

    public static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    private final CarrierRepository carrierRepository;
    private final ParcelRepository parcelRepository;

    public TrackController(CarrierRepository carrierRepository, ParcelRepository parcelRepository) {
        this.carrierRepository = carrierRepository;
        this.parcelRepository = parcelRepository;
    }

    @CrossOrigin
    @Operation(summary = "Health of the server")
    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Login for carriers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Credentials are correct"), @ApiResponse(responseCode = "404", description = "Credentials doesn't match with any user in the database")})
    @GetMapping("/carriers")
    public ResponseEntity<Void> login(@RequestHeader("User") String user, @RequestHeader("Password") String password) {
        boolean correctCredentials = carrierRepository.findAll().stream().filter(carrier -> carrier.getName().equalsIgnoreCase(user) && Objects.equals(carrier.getPassword(), password)).map(anyCarrier -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);
        return correctCredentials ? new ResponseEntity<>(null, HttpStatus.OK) : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

    }

    @CrossOrigin
    @Operation(summary = "Bulk upload of parcels for carriers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Upload was successful"), @ApiResponse(responseCode = "412", description = "The carrier provided in not registered in the system"), @ApiResponse(responseCode = "406", description = "There is an error in any of the status provided for the parcels"), @ApiResponse(responseCode = "409", description = "There are duplicates within the tracking numbers provided and the system"),})
    @PostMapping("/parcels/{carrierName}")
    public ResponseEntity<Void> loadParcels(@PathVariable String carrierName, @RequestParam("parcels") MultipartFile file) throws IOException {
        if (!isCarrierInDatabase(carrierName)) {
            return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
        }

        List<Parcel> parcels = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        CsvParserSettings setting = new CsvParserSettings();
        setting.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(setting);
        List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
        parseAllRecords.forEach(record -> {
            Parcel parcel = new Parcel();
            parcel.setTrackingNumber(record.getString("tracking_number"));
            parcel.setStatus(record.getString("status"));
            parcels.add(parcel);
        });

        if (findDuplicateTrackingNumbers(parcels)) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        } else if (findIncorrectStatuses(parcels)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        } else {
            parcelRepository.saveAll(parcels);
            LOGGER.log(INFO, "Parcels have been loaded successfully");
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @CrossOrigin
    @Operation(summary = "Remove all parcels for a carrier")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deletion was successful"), @ApiResponse(responseCode = "412", description = "The carrier provided in not registered in the system"),})
    @DeleteMapping("/parcels/{carrierName}")
    public ResponseEntity<Void> deleteParcels(@PathVariable String carrierName) {
        if (isCarrierInDatabase(carrierName)) {
            return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
        }
        parcelRepository.deleteAll();
        LOGGER.log(INFO, "Parcels have been deleted successfully");
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

    }

    @CrossOrigin
    @Operation(summary = "Get parcel information given its tracking number")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parcel found and data retrieved"), @ApiResponse(responseCode = "404", description = "Parcel not found")})
    @GetMapping("/parcels/{trackingNumber}")
    public ResponseEntity<Parcel> getParcelInfo(@PathVariable String trackingNumber) {

        List<Parcel> parcels = parcelRepository.findAll().stream().filter(parcelFound -> Objects.equals(parcelFound.getTrackingNumber(), trackingNumber)).collect(Collectors.toList());
        return parcels.size() == 1 ? new ResponseEntity<>(parcels.get(0), HttpStatus.OK) : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

    }

    private boolean isCarrierInDatabase(String carrierName) {
        return carrierRepository.findAll().stream().anyMatch(aCarrier -> Objects.equals(aCarrier.getName(), carrierName));
    }

    private boolean findDuplicateTrackingNumbers(List<Parcel> inputParcels) {
        return findDuplicateTrackingNumbersAmongInput(inputParcels) || findDuplicateTrackingNumbersWithCurrents(inputParcels);
    }

    private boolean findDuplicateTrackingNumbersWithCurrents(List<Parcel> inputParcels) {
        List<String> currentTrackingNumbers = parcelRepository.findAll().stream().map(Parcel::getTrackingNumber).collect(Collectors.toList());

        boolean duplicateTrackingNumberWithCurrents = inputParcels.stream().map(Parcel::getTrackingNumber).filter(currentTrackingNumbers::contains).map(trackingNumber -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);

        if (duplicateTrackingNumberWithCurrents)
            LOGGER.log(WARNING, "There are parcels with a tracking number already present in the database");

        return duplicateTrackingNumberWithCurrents;
    }

    private boolean findDuplicateTrackingNumbersAmongInput(List<Parcel> inputParcels) {
        List<String> currentTrackingNumbersAsList = inputParcels.stream().map(Parcel::getTrackingNumber).collect(Collectors.toList());

        Set<String> currentTrackingNumbersAsSet = inputParcels.stream().map(Parcel::getTrackingNumber).collect(Collectors.toSet());

        boolean duplicateTrackingNumbersAmongInputs = currentTrackingNumbersAsList.size() != currentTrackingNumbersAsSet.size();

        if (duplicateTrackingNumbersAmongInputs)
            LOGGER.log(WARNING, "There are parcels with the same tracking number in the input list");

        return duplicateTrackingNumbersAmongInputs;
    }

    private boolean findIncorrectStatuses(List<Parcel> inputParcels) {
        List<String> correctStatuses = List.of("En tránsito", "Entregado", "Error en la entrega");
        boolean incorrectStatus = inputParcels.stream().map(Parcel::getStatus).filter(status -> !correctStatuses.contains(status)).map(status -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);

        if (incorrectStatus) LOGGER.log(WARNING, "There are parcels with an incorrect status");

        return incorrectStatus;
    }

}