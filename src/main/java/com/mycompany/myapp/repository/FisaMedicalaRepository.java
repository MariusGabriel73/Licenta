package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.FisaMedicala;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the FisaMedicala entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FisaMedicalaRepository extends ReactiveCrudRepository<FisaMedicala, Long>, FisaMedicalaRepositoryInternal {
    Flux<FisaMedicala> findAllBy(Pageable pageable);

    @Query("SELECT * FROM fisa_medicala entity WHERE entity.programare_id = :id")
    Flux<FisaMedicala> findByProgramare(Long id);

    @Query("SELECT * FROM fisa_medicala entity WHERE entity.programare_id IS NULL")
    Flux<FisaMedicala> findAllWhereProgramareIsNull();

    @Override
    <S extends FisaMedicala> Mono<S> save(S entity);

    @Override
    Flux<FisaMedicala> findAll();

    @Override
    Mono<FisaMedicala> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FisaMedicalaRepositoryInternal {
    <S extends FisaMedicala> Mono<S> save(S entity);

    Flux<FisaMedicala> findAllBy(Pageable pageable);

    Flux<FisaMedicala> findAll();

    Mono<FisaMedicala> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<FisaMedicala> findAllBy(Pageable pageable, Criteria criteria);
}
