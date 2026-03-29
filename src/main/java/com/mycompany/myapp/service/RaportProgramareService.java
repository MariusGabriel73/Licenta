package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.RaportProgramareDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.RaportProgramare}.
 */
public interface RaportProgramareService {
    /**
     * Save a raportProgramare.
     *
     * @param raportProgramareDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RaportProgramareDTO> save(RaportProgramareDTO raportProgramareDTO);

    /**
     * Updates a raportProgramare.
     *
     * @param raportProgramareDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RaportProgramareDTO> update(RaportProgramareDTO raportProgramareDTO);

    /**
     * Partially updates a raportProgramare.
     *
     * @param raportProgramareDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RaportProgramareDTO> partialUpdate(RaportProgramareDTO raportProgramareDTO);

    /**
     * Get all the raportProgramares.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RaportProgramareDTO> findAll(Pageable pageable);

    /**
     * Returns the number of raportProgramares available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" raportProgramare.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RaportProgramareDTO> findOne(Long id);

    /**
     * Delete the "id" raportProgramare.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
