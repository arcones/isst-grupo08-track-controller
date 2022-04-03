package es.upm.isst.grupo08.trackback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.upm.isst.grupo08.trackback.repository.CarrierRepository;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.*;

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
    @Operation(summary = "Login for carriers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Credentials are correct"), @ApiResponse(responseCode = "404", description = "Credentials doesn't match with any user in the database")})
    @GetMapping("/carriers")
    public ResponseEntity<Void> login(@RequestHeader("User") String user, @RequestHeader("Password") String password) {
        try {
            boolean correctCredentials = carrierRepository.findAll().stream().filter(carrier -> carrier.getName().equalsIgnoreCase(user) && Objects.equals(carrier.getPassword(), password)).map(anyCarrier -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);
            return correctCredentials ? new ResponseEntity<>(null, HttpStatus.OK) : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.log(SEVERE, "Exception arose in GET /carriers request: \n " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin
    @Operation(summary = "Bulk upload of parcels for carriers")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Upload was successful"), @ApiResponse(responseCode = "412", description = "There is an error in the carrier IDs provided"), @ApiResponse(responseCode = "406", description = "There is an error in any of the status provided for the parcels"), @ApiResponse(responseCode = "409", description = "There are duplicates within the tracking numbers provided and the system"),})
    @PostMapping("/parcels")
    public ResponseEntity<Void> loadParcels(@RequestBody String body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String cleanParcels = body.replace("{\"data\":", "").replace("]}", "]");

            List<Parcel> inputParcels = Arrays.asList(mapper.readValue(cleanParcels, Parcel[].class));

            if (findDuplicateTrackingNumbers(inputParcels)) {
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            } else if (findIncorrectCarriers(inputParcels)) {
                return new ResponseEntity<>(null, HttpStatus.PRECONDITION_FAILED);
            } else if (findIncorrectStatuses(inputParcels)) {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            } else {
                parcelRepository.saveAll(inputParcels);
                LOGGER.log(INFO, "Parcels have been loaded successfully");
                return new ResponseEntity<>(null, HttpStatus.OK);
            }
        } catch (Exception e) {
            LOGGER.log(SEVERE, "Exception arose in POST /parcels request: \n " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @CrossOrigin
    @Operation(summary = "Get parcel information given its tracking number")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Parcel found and data retrieved"), @ApiResponse(responseCode = "404", description = "Parcel not found")})
    @GetMapping("/parcels/{trackingNumber}")
    public ResponseEntity<Parcel> getParcelInfo(@PathVariable String trackingNumber) {
        try {
            List<Parcel> parcels = parcelRepository.findAll().stream().filter(parcelFound -> Objects.equals(parcelFound.getTrackingNumber(), trackingNumber)).collect(Collectors.toList());
            return parcels.size() == 1 ? new ResponseEntity<>(parcels.get(0), HttpStatus.OK) : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            LOGGER.log(SEVERE, "Exception arose in GET /parcels/{trackingNumber} request: \n " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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


    private boolean findIncorrectCarriers(List<Parcel> inputParcels) {
        List<Long> currentCarriers = parcelRepository.findAll().stream().map(Parcel::getCarrierId).collect(Collectors.toList());

        boolean incorrectCarrier = inputParcels.stream().map(Parcel::getCarrierId).filter(carrier -> !currentCarriers.contains(carrier)).map(inputCarrier -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);

        if (incorrectCarrier)
            LOGGER.log(WARNING, "There are parcels with a carrier that doesn't exist in the database");

        return incorrectCarrier;
    }

    private boolean findIncorrectStatuses(List<Parcel> inputParcels) {
        List<String> correctStatuses = List.of("En tránsito", "Entregado", "Error en la entrega");
        boolean incorrectStatus = inputParcels.stream().map(Parcel::getStatus).filter(status -> !correctStatuses.contains(status)).map(status -> Boolean.TRUE).findAny().orElse(Boolean.FALSE);

        if (incorrectStatus) LOGGER.log(WARNING, "There are parcels with an incorrect status");

        return incorrectStatus;
    }

}