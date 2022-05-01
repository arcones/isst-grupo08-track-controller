package es.upm.isst.grupo08.trackback.controller;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import es.upm.isst.grupo08.trackback.controller.error.carrierNotFound.CarrierNotFoundException;
import es.upm.isst.grupo08.trackback.controller.error.duplicateParcelsInFile.DuplicateParcelsException;
import es.upm.isst.grupo08.trackback.controller.error.parcelNotFound.ParcelNotFoundException;
import es.upm.isst.grupo08.trackback.controller.error.wrongCredentials.WrongCredentialsException;
import es.upm.isst.grupo08.trackback.controller.error.wrongStatuses.WrongStatusesException;
import es.upm.isst.grupo08.trackback.model.ApplicationUser;
import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.UserRepository;
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
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

@RestController
@Tag(name = "Track", description = "The Trackermaster API")
public class TrackController {

    public static final Logger LOGGER = Logger.getLogger(TrackController.class.getName());

    private final UserRepository userRepository;
    private final ParcelRepository parcelRepository;

    public TrackController(UserRepository userRepository, ParcelRepository parcelRepository) {
        this.userRepository = userRepository;
        this.parcelRepository = parcelRepository;
    }

    @CrossOrigin
    @Operation(summary = "Health of the server")
    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Login for users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credentials are correct"),
            @ApiResponse(responseCode = "404", description = "Credentials doesn't match with any user in the database")
    })
    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestHeader("User") String user, @RequestHeader("Password") String password) {
        checkCredentialsCorrectness(user, password);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Bulk upload of parcels for carriers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload was successful"),
            @ApiResponse(responseCode = "412", description = "The carrier provided in not registered in the system"),
            @ApiResponse(responseCode = "406", description = "There is an error in any of the status provided for the parcels"),
            @ApiResponse(responseCode = "409", description = "There are duplicates within the tracking numbers provided and the system")
    })
    @PostMapping("/parcels/{carrierName}")
    public ResponseEntity<Void> loadParcels(@PathVariable String carrierName, @RequestParam("parcels") MultipartFile parcelsCSV) throws IOException {
        List<Parcel> parcels = parseParcelsCSV(carrierName, parcelsCSV);

        checkForDuplicateParcels(parcels);
        checkForWrongStatuses(parcels);

        parcelRepository.saveAll(parcels);
        LOGGER.log(INFO, "Parcels have been loaded successfully");
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @CrossOrigin
    @Operation(summary = "Remove all parcels for a carrier")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Deletion was successful"), @ApiResponse(responseCode = "412", description = "The carrier provided in not registered in the system"),})
    @DeleteMapping("/parcels/{carrierName}")
    public ResponseEntity<Void> deleteParcels(@PathVariable String carrierName) {
        ApplicationUser applicationUser = checkForCarrierExistence(carrierName);
        List<Long> parcelIdsToRemove = parcelRepository.findAll().stream().filter(parcel -> parcel.getCarrierId() == applicationUser.getId()).map(Parcel::getId).collect(Collectors.toList());
        parcelRepository.deleteAllById(parcelIdsToRemove);
        LOGGER.log(INFO, "Parcels have been deleted successfully");
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @CrossOrigin
    @Operation(summary = "Get parcel information given its tracking number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parcel found and data retrieved"),
            @ApiResponse(responseCode = "404", description = "Parcel not found")
    })
    @GetMapping("/parcels/{trackingNumber}")
    public ResponseEntity<Parcel> getParcelInfo(@PathVariable String trackingNumber) {
        List<Parcel> parcels = parcelRepository.findAll().stream().filter(parcelFound -> Objects.equals(parcelFound.getTrackingNumber(), trackingNumber)).collect(Collectors.toList());
        checkParcelValidity(parcels);
        return new ResponseEntity<>(parcels.get(0), HttpStatus.OK);
    }

    private void checkParcelValidity(List<Parcel> parcels) {
        if (parcels.size() == 0) throw new ParcelNotFoundException();
    }

    private void checkCredentialsCorrectness(String user, String password) {
        boolean correctCredentials = userRepository.findAll().stream().filter(carrier -> carrier.getName().equalsIgnoreCase(user) && Objects.equals(carrier.getPassword(), password)).map(anyApplicationUser -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);
        if (!correctCredentials) {
            LOGGER.log(WARNING, "Credentials are not correct");
            throw new WrongCredentialsException();
        }
    }

    private List<Parcel> parseParcelsCSV(String carrierName, MultipartFile file) throws IOException {
        ApplicationUser applicationUser = checkForCarrierExistence(carrierName);

        CsvParserSettings setting = new CsvParserSettings();
        setting.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(setting);

        LOGGER.log(INFO, "Parsing CSV file with parcels...");

        List<Record> parseAllRecords = parser.parseAllRecords(file.getInputStream());

        List<Parcel> parcels = new ArrayList<>();
        parseAllRecords.forEach(record -> {
            parcels.add(new Parcel(
                    record.getString("tracking_number"),
                    applicationUser.getId(),
                    record.getString("status"),
                    record.getString("recipient")
            )); //TODO integridad referencial, por ejemplo aqui me deja meterlos sin carrier y no debería poderse. O sin user (que deberia ir por ID)
        });

        LOGGER.log(INFO, "The CSV file has been parsed successfully");

        return parcels;
    }

    private ApplicationUser checkForCarrierExistence(String carrierName) {
        return userRepository.findAll().stream()
                .filter(aApplicationUser -> Objects.equals(aApplicationUser.getName(), carrierName))
                .findAny()
                .orElseThrow(CarrierNotFoundException::new);
    }

    private void checkForDuplicateParcels(List<Parcel> inputParcels) {
        if (findDuplicateTrackingNumbersAmongInput(inputParcels) || findDuplicateTrackingNumbersWithCurrents(inputParcels))
            throw new DuplicateParcelsException();
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

    private void checkForWrongStatuses(List<Parcel> inputParcels) {
        List<String> correctStatuses = List.of("En tránsito", "Entregado", "Error en la entrega");
        boolean incorrectStatus = inputParcels.stream().map(Parcel::getStatus).filter(status -> !correctStatuses.contains(status)).map(status -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);

        if (incorrectStatus) {
            LOGGER.log(WARNING, "There are parcels with an incorrect status");
            throw new WrongStatusesException();
        }
    }

}