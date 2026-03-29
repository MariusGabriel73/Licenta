package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Pacient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Pacient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PacientRepository extends ReactiveCrudRepository<Pacient, Long>, PacientRepositoryInternal {
    Flux<Pacient> findAllBy(Pageable pageable);

    @Query("SELECT * FROM pacient entity WHERE entity.user_id = :id")
    Flux<Pacient> findByUser(Long id);

    @Query("SELECT * FROM pacient entity WHERE entity.user_id IS NULL")
    Flux<Pacient> findAllWhereUserIsNull();

    @Query(
        "SELECT p.* FROM pacient p JOIN jhi_user u ON u.id = p.user_id WHERE u.login = :login"
    )
    Mono<Pacient> findByUserLogin(String login);

    @Override
    <S extends Pacient> Mono<S> save(S entity);

    @Override
    Flux<Pacient> findAll();

    @Override
    Mono<Pacient> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PacientRepositoryInternal {
    <S extends Pacient> Mono<S> save(S entity);

    Flux<Pacient> findAllBy(Pageable pageable);

    Flux<Pacient> findAll();

    Mono<Pacient> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pacient> findAllBy(Pageable pageable, Criteria criteria);
}
