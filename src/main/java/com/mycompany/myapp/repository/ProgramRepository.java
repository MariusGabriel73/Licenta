package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Program;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Program entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProgramRepository extends ReactiveCrudRepository<Program, Long>, ProgramRepositoryInternal {
    Flux<Program> findAllBy(Pageable pageable);

    @Query("SELECT * FROM program entity WHERE entity.medic_id = :medicId AND entity.locatie_id = :locatieId")
    Flux<Program> findByMedicAndLocatie(Long medicId, Long locatieId);

    @Query("SELECT * FROM program entity WHERE entity.medic_id = :id")
    Flux<Program> findByMedic(Long id);

    @Query("SELECT * FROM program entity WHERE entity.medic_id IS NULL")
    Flux<Program> findAllWhereMedicIsNull();

    @Query("SELECT * FROM program entity WHERE entity.locatie_id = :id")
    Flux<Program> findByLocatie(Long id);

    @Query("SELECT * FROM program entity WHERE entity.locatie_id IS NULL")
    Flux<Program> findAllWhereLocatieIsNull();

    @Override
    <S extends Program> Mono<S> save(S entity);

    @Override
    Flux<Program> findAll();

    @Override
    Mono<Program> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProgramRepositoryInternal {
    <S extends Program> Mono<S> save(S entity);

    Flux<Program> findAllBy(Pageable pageable);

    Flux<Program> findAll();

    Mono<Program> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Program> findAllBy(Pageable pageable, Criteria criteria);
}
