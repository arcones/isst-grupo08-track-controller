package es.upm.isst.grupo08.trackback.controller;

import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.upm.isst.grupo08.trackback.repository.CarrierRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class TrackController {

    private final CarrierRepository carrierRepository;
    private final ParcelRepository parcelRepository;

    public TrackController(CarrierRepository carrierRepository, ParcelRepository parcelRepository) {
        this.carrierRepository = carrierRepository;
        this.parcelRepository = parcelRepository;
    }

    @GetMapping("/carriers")
    public ResponseEntity<Void> login(@RequestHeader("User") String user, @RequestHeader("Password") String password) {
        try {
            boolean correctCredentials = carrierRepository.findAll().stream()
                    .filter(carrier -> carrier.getName().equalsIgnoreCase(user) && Objects.equals(carrier.getPassword(), password))
                    .map(anyCarrier -> Boolean.TRUE)
                    .findAny()
                    .orElse(Boolean.FALSE);
            return correctCredentials ? new ResponseEntity<>(null, HttpStatus.OK) : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/parcels")
    public ResponseEntity<Void> loadParcels(@RequestBody List<Parcel> parcels) {
        try {
            parcelRepository.saveAll(parcels);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/parcels/{trackingNumber}")
    public ResponseEntity<Parcel> getParcelInfo(@PathVariable String trackingNumber) {
        try {
            List<Parcel> parcels = parcelRepository.findAll().stream()
                    .filter(parcelFound -> Objects.equals(parcelFound.getTrackingNumber(), trackingNumber))
                    .collect(Collectors.toList());
            return parcels.size() == 1 ? new ResponseEntity<>(parcels.get(0), HttpStatus.OK) : new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}