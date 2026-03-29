package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.SpecializareDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Specializare}.
 */
public interface SpecializareService {
    /**
     * Save a specializare.
     *
     * @param specializareDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<SpecializareDTO> save(SpecializareDTO specializareDTO);

    /**
     * Updates a specializare.
     *
     * @param specializareDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<SpecializareDTO> update(SpecializareDTO specializareDTO);

    /**
     * Partially updates a specializare.
     *
     * @param specializareDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<SpecializareDTO> partialUpdate(SpecializareDTO specializareDTO);

    /**
     * Get all the specializares.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<SpecializareDTO> findAll(Pageable pageable);

    /**
     * Returns the number of specializares available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" specializare.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<SpecializareDTO> findOne(Long id);

    /**
     * Delete the "id" specializare.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
