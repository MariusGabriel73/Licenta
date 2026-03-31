package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.LocatieDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Locatie}.
 */
public interface LocatieService {
    /**
     * Save a locatie.
     *
     * @param locatieDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<LocatieDTO> save(LocatieDTO locatieDTO);

    /**
     * Updates a locatie.
     *
     * @param locatieDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<LocatieDTO> update(LocatieDTO locatieDTO);

    /**
     * Partially updates a locatie.
     *
     * @param locatieDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<LocatieDTO> partialUpdate(LocatieDTO locatieDTO);

    /**
     * Get all the locaties.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<LocatieDTO> findAll(Pageable pageable);

    /**
     * Get the list of locaties for a specific clinic.
     * @param clinicaId the clinic ID.
     * @return the list of entities.
     */
    Flux<LocatieDTO> findByCliniciId(Long clinicaId);

    /**
     * Returns the number of locaties available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" locatie.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<LocatieDTO> findOne(Long id);

    /**
     * Delete the "id" locatie.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
