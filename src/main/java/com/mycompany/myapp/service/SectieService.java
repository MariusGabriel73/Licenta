package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.SectieDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Sectie}.
 */
public interface SectieService {
    /**
     * Save a sectie.
     *
     * @param sectieDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SectieDTO> save(SectieDTO sectieDTO);

    /**
     * Updates a sectie.
     *
     * @param sectieDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SectieDTO> update(SectieDTO sectieDTO);

    /**
     * Partially updates a sectie.
     *
     * @param sectieDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SectieDTO> partialUpdate(SectieDTO sectieDTO);

    /**
     * Get all the secties.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SectieDTO> findAll(Pageable pageable);

    /**
     * Returns the number of secties available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" sectie.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SectieDTO> findOne(Long id);

    /**
     * Delete the "id" sectie.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
