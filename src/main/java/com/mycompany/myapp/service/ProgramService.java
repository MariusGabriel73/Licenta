package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ProgramDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Program}.
 */
public interface ProgramService {
    /**
     * Save a program.
     *
     * @param programDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProgramDTO> save(ProgramDTO programDTO);

    /**
     * Updates a program.
     *
     * @param programDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProgramDTO> update(ProgramDTO programDTO);

    /**
     * Partially updates a program.
     *
     * @param programDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProgramDTO> partialUpdate(ProgramDTO programDTO);

    /**
     * Get all the programs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProgramDTO> findAll(Pageable pageable);

    /**
     * Get programs filtered by medic and location.
     */
    Flux<ProgramDTO> findByMedicAndLocatie(Long medicId, Long locatieId);

    /**
     * Returns the number of programs available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" program.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProgramDTO> findOne(Long id);

    /**
     * Delete the "id" program.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
