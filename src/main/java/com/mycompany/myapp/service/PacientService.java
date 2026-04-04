package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PacientDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Pacient}.
 */
public interface PacientService {
    /**
     * Save a pacient.
     *
     * @param pacientDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PacientDTO> save(PacientDTO pacientDTO);

    /**
     * Updates a pacient.
     *
     * @param pacientDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PacientDTO> update(PacientDTO pacientDTO);

    /**
     * Partially updates a pacient.
     *
     * @param pacientDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PacientDTO> partialUpdate(PacientDTO pacientDTO);

    /**
     * Get all the pacients.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<PacientDTO> findAll(Pageable pageable);

    /**
     * Returns the number of pacients available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" pacient.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PacientDTO> findOne(Long id);

    /**
     * Delete the "id" pacient.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Find the Pacient for a given user login.
     */
    Mono<PacientDTO> findByUserLogin(String login);

    /**
     * Find the Pacient for a given user ID.
     */
    Mono<PacientDTO> findByUserId(Long userId);
}
