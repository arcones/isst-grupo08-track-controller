package es.upm.isst.grupo08.trackback.controller;

import es.upm.isst.grupo08.trackback.model.Carrier;
import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.upm.isst.grupo08.trackback.repository.CarrierRepository;

import java.util.List;


@RestController
public class TrackController {

    private final CarrierRepository carrierRepository;
    private final ParcelRepository parcelRepository;

    public TrackController(CarrierRepository carrierRepository, ParcelRepository parcelRepository) {
        this.carrierRepository = carrierRepository;
        this.parcelRepository = parcelRepository;
    }

    @GetMapping("/carriers/{id}")
    public ResponseEntity<Carrier> getById(@RequestParam long id) {
        try {
            Carrier carrier = carrierRepository.findById(id);
            return new ResponseEntity<>(carrier, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/parcels")
    public ResponseEntity<List<Parcel>> insertParcels(@RequestBody List<Parcel> parcels) {
        parcelRepository.saveAll(parcels);
        return new ResponseEntity<>(parcels, HttpStatus.OK);
    }

}