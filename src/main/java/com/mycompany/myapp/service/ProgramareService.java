package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ProgramareDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Programare}.
 */
public interface ProgramareService {
    /**
     * Save a programare.
     *
     * @param programareDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProgramareDTO> save(ProgramareDTO programareDTO);

    /**
     * Updates a programare.
     *
     * @param programareDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProgramareDTO> update(ProgramareDTO programareDTO);

    /**
     * Partially updates a programare.
     *
     * @param programareDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProgramareDTO> partialUpdate(ProgramareDTO programareDTO);

    /**
     * Get all the programares.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProgramareDTO> findAll(Pageable pageable);

    Flux<ProgramareDTO> findAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate, Pageable pageable);

    Flux<ProgramareDTO> findByMedicIdAndLocatieIdAndDataProgramareBetween(
        Long medicId,
        Long locatieId,
        java.time.Instant fromDate,
        java.time.Instant toDate
    );

    /**
     * Get all the ProgramareDTO where FisaMedicala is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<ProgramareDTO> findAllWhereFisaMedicalaIsNull();
    /**
     * Get all the ProgramareDTO where RaportProgramare is {@code null}.
     *
     * @return the {@link Flux} of entities.
     */
    Flux<ProgramareDTO> findAllWhereRaportProgramareIsNull();

    /**
     * Returns the number of programares available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    Mono<Long> countAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate);

    Flux<ProgramareDTO> findAllByPacientId(Long pacientId, Pageable pageable);

    Mono<Long> countAllByPacientId(Long pacientId);

    Flux<ProgramareDTO> findAllByPacientUserLogin(String login, Pageable pageable);

    Mono<Long> countAllByPacientUserLogin(String login);

    /**
     * Get the "id" programare.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProgramareDTO> findOne(Long id);

    /**
     * Delete the "id" programare.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
