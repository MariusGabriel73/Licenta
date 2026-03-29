package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Sectie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Sectie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SectieRepository extends ReactiveCrudRepository<Sectie, Long>, SectieRepositoryInternal {
    Flux<Sectie> findAllBy(Pageable pageable);

    @Override
    <S extends Sectie> Mono<S> save(S entity);

    @Override
    Flux<Sectie> findAll();

    @Override
    Mono<Sectie> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface SectieRepositoryInternal {
    <S extends Sectie> Mono<S> save(S entity);

    Flux<Sectie> findAllBy(Pageable pageable);

    Flux<Sectie> findAll();

    Mono<Sectie> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Sectie> findAllBy(Pageable pageable, Criteria criteria);
}
