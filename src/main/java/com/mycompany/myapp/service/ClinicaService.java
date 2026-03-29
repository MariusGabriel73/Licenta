package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ClinicaDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Clinica}.
 */
public interface ClinicaService {
    /**
     * Save a clinica.
     *
     * @param clinicaDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ClinicaDTO> save(ClinicaDTO clinicaDTO);

    /**
     * Updates a clinica.
     *
     * @param clinicaDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ClinicaDTO> update(ClinicaDTO clinicaDTO);

    /**
     * Partially updates a clinica.
     *
     * @param clinicaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ClinicaDTO> partialUpdate(ClinicaDTO clinicaDTO);

    /**
     * Get all the clinicas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ClinicaDTO> findAll(Pageable pageable);

    /**
     * Find all clinicas belonging to a given location.
     *
     * @param locatieId the location id to filter by.
     * @return the list of entities.
     */
    Flux<ClinicaDTO> findByLocatieId(Long locatieId);

    /**
     * Find all clinicas belonging to a medic's user login.
     *
     * @param login the user login to filter by.
     * @return the list of entities.
     */
    Flux<ClinicaDTO> findByMedicUserLogin(String login);

    /**
     * Returns the number of clinicas available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" clinica.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ClinicaDTO> findOne(Long id);

    /**
     * Delete the "id" clinica.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
