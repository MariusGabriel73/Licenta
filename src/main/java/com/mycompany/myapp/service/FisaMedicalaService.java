package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.FisaMedicalaDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.FisaMedicala}.
 */
public interface FisaMedicalaService {
    /**
     * Save a fisaMedicala.
     *
     * @param fisaMedicalaDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<FisaMedicalaDTO> save(FisaMedicalaDTO fisaMedicalaDTO);

    /**
     * Updates a fisaMedicala.
     *
     * @param fisaMedicalaDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<FisaMedicalaDTO> update(FisaMedicalaDTO fisaMedicalaDTO);

    /**
     * Partially updates a fisaMedicala.
     *
     * @param fisaMedicalaDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<FisaMedicalaDTO> partialUpdate(FisaMedicalaDTO fisaMedicalaDTO);

    /**
     * Get all the fisaMedicalas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<FisaMedicalaDTO> findAll(Pageable pageable);

    /**
     * Returns the number of fisaMedicalas available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" fisaMedicala.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<FisaMedicalaDTO> findOne(Long id);

    /**
     * Delete the "id" fisaMedicala.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
