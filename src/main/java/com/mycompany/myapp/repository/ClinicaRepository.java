package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clinica;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Clinica entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClinicaRepository extends ReactiveCrudRepository<Clinica, Long>, ClinicaRepositoryInternal {
    Flux<Clinica> findAllBy(Pageable pageable);

    // Method implemented in ClinicaRepositoryInternalImpl to ensure Locatie is eager-loaded
    Flux<Clinica> findByLocatie(Long id);

    @Query("SELECT * FROM clinica entity WHERE entity.locatie_id IS NULL")
    Flux<Clinica> findAllWhereLocatieIsNull();

    @Query(
        "SELECT c.* FROM clinica c " +
        "JOIN rel_medic__clinici r ON c.id = r.clinici_id " +
        "JOIN medic m ON m.id = r.medic_id " +
        "JOIN jhi_user u ON u.id = m.user_id " +
        "WHERE u.login = :login"
    )
    Flux<Clinica> findByMedicUserLogin(String login);

    @Override
    <S extends Clinica> Mono<S> save(S entity);

    @Override
    Flux<Clinica> findAll();

    @Override
    Mono<Clinica> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ClinicaRepositoryInternal {
    <S extends Clinica> Mono<S> save(S entity);

    Flux<Clinica> findAllBy(Pageable pageable);

    Flux<Clinica> findAll();

    Mono<Clinica> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Clinica> findAllBy(Pageable pageable, Criteria criteria);

    Flux<Clinica> findByLocatie(Long id);
}
