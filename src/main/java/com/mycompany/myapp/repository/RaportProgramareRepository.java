package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RaportProgramare;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RaportProgramare entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RaportProgramareRepository extends ReactiveCrudRepository<RaportProgramare, Long>, RaportProgramareRepositoryInternal {
    Flux<RaportProgramare> findAllBy(Pageable pageable);

    @Query("SELECT * FROM raport_programare entity WHERE entity.programare_id = :id")
    Flux<RaportProgramare> findByProgramare(Long id);

    @Query("SELECT * FROM raport_programare entity WHERE entity.programare_id IS NULL")
    Flux<RaportProgramare> findAllWhereProgramareIsNull();

    @Override
    <S extends RaportProgramare> Mono<S> save(S entity);

    @Override
    Flux<RaportProgramare> findAll();

    @Override
    Mono<RaportProgramare> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RaportProgramareRepositoryInternal {
    <S extends RaportProgramare> Mono<S> save(S entity);

    Flux<RaportProgramare> findAllBy(Pageable pageable);

    Flux<RaportProgramare> findAll();

    Mono<RaportProgramare> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RaportProgramare> findAllBy(Pageable pageable, Criteria criteria);
}
