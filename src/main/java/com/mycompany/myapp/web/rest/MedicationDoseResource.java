package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.MedicationDose;
import com.mycompany.myapp.service.MedicationDoseService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing MedicationDose.
 */
@RestController
@RequestMapping("/api/medication-doses")
public class MedicationDoseResource {

    private static final Logger LOG = LoggerFactory.getLogger(MedicationDoseResource.class);

    private static final String ENTITY_NAME = "medicationDose";

    private final MedicationDoseService medicationDoseService;

    public MedicationDoseResource(MedicationDoseService medicationDoseService) {
        this.medicationDoseService = medicationDoseService;
    }

    /**
     * {@code GET  /medication-doses/pacient/:id} : Get all medication doses for a patient.
     */
    @GetMapping("/pacient/{id}")
    public Mono<List<MedicationDose>> getDosesByPacient(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MedicationDose for pacient : {}", id);
        return medicationDoseService.getDosesByPacient(id).collectList();
    }

    /**
     * {@code PATCH  /medication-doses/:id/confirm} : Confirm that a dose has been taken.
     */
    @PatchMapping("/{id}/confirm")
    public Mono<ResponseEntity<MedicationDose>> confirmDose(@PathVariable("id") Long id) {
        LOG.debug("REST request to confirm MedicationDose : {}", id);
        return medicationDoseService
            .confirmDose(id)
            .map(result ->
                ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert("medicalSystem", true, ENTITY_NAME, result.getId().toString()))
                    .body(result)
            );
    }
}
