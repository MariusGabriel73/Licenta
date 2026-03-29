package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.MedicDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Medic}.
 */
public interface MedicService {
    /**
     * Save a medic.
     *
     * @param medicDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<MedicDTO> save(MedicDTO medicDTO);

    /**
     * Updates a medic.
     *
     * @param medicDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<MedicDTO> update(MedicDTO medicDTO);

    /**
     * Partially updates a medic.
     *
     * @param medicDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<MedicDTO> partialUpdate(MedicDTO medicDTO);

    /**
     * Get all the medics.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MedicDTO> findAll(Pageable pageable);

    /**
     * Get all the medics with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<MedicDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Cascade filter: get medics by clinic and optionally by department.
     * Used for the appointment creation cascading dropdown (Step D).
     *
     * @param clinicaId the clinic id (required).
     * @param sectieId  the department id (optional, null for all departments).
     * @return the filtered list of entities.
     */
    Flux<MedicDTO> findByCascadeFilter(Long clinicaId, Long sectieId);

    /**
     * Returns the number of medics available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" medic.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<MedicDTO> findOne(Long id);

    /**
     * Delete the "id" medic.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
