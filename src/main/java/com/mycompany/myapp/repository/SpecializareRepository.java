package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Specializare;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Specializare entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SpecializareRepository extends ReactiveCrudRepository<Specializare, Long>, SpecializareRepositoryInternal {
    Flux<Specializare> findAllBy(Pageable pageable);

    @Override
    <S extends Specializare> Mono<S> save(S entity);

    @Override
    Flux<Specializare> findAll();

    @Override
    Mono<Specializare> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SpecializareRepositoryInternal {
    <S extends Specializare> Mono<S> save(S entity);

    Flux<Specializare> findAllBy(Pageable pageable);

    Flux<Specializare> findAll();

    Mono<Specializare> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Specializare> findAllBy(Pageable pageable, Criteria criteria);
}
