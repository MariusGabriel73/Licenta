package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Locatie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Locatie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocatieRepository extends ReactiveCrudRepository<Locatie, Long>, LocatieRepositoryInternal {
    Flux<Locatie> findAllBy(Pageable pageable);

    @Override
    <S extends Locatie> Mono<S> save(S entity);

    @Override
    Flux<Locatie> findAll();

    @Override
    Mono<Locatie> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface LocatieRepositoryInternal {
    <S extends Locatie> Mono<S> save(S entity);

    Flux<Locatie> findAllBy(Pageable pageable);

    Flux<Locatie> findAll();

    Mono<Locatie> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Locatie> findAllBy(Pageable pageable, Criteria criteria);
}
