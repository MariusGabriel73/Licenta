package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Medic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Medic entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedicRepository extends ReactiveCrudRepository<Medic, Long>, MedicRepositoryInternal {
    Flux<Medic> findAllBy(Pageable pageable);

    @Override
    Mono<Medic> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Medic> findAllWithEagerRelationships();

    @Override
    Flux<Medic> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM medic entity WHERE entity.user_id = :id")
    Flux<Medic> findByUser(Long id);

    @Query("SELECT * FROM medic entity WHERE entity.user_id IS NULL")
    Flux<Medic> findAllWhereUserIsNull();

    @Query("SELECT * FROM medic entity WHERE entity.sectie_id = :id")
    Flux<Medic> findBySectie(Long id);

    @Query("SELECT * FROM medic entity WHERE entity.sectie_id IS NULL")
    Flux<Medic> findAllWhereSectieIsNull();

    @Query(
        "SELECT entity.* FROM medic entity JOIN rel_medic__clinici joinTable ON entity.id = joinTable.clinici_id WHERE joinTable.clinici_id = :id"
    )
    Flux<Medic> findByClinici(Long id);

    @Query(
        "SELECT entity.* FROM medic entity JOIN rel_medic__specializari joinTable ON entity.id = joinTable.specializari_id WHERE joinTable.specializari_id = :id"
    )
    Flux<Medic> findBySpecializari(Long id);

    /**
     * Cascading filter: find medics by clinic AND optionally by department.
     * Used for the appointment creation form Step D dropdown.
     */
    // Method implemented in MedicRepositoryInternalImpl to ensure User is eager-loaded
    Flux<Medic> findByCascadeFilter(Long clinicaId, Long sectieId);

    @Override
    <S extends Medic> Mono<S> save(S entity);

    @Override
    Flux<Medic> findAll();

    @Override
    Mono<Medic> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface MedicRepositoryInternal {
    <S extends Medic> Mono<S> save(S entity);

    Flux<Medic> findAllBy(Pageable pageable);

    Flux<Medic> findAll();

    Mono<Medic> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Medic> findAllBy(Pageable pageable, Criteria criteria);

    Flux<Medic> findByCascadeFilter(Long clinicaId, Long sectieId);

    Mono<Medic> findOneWithEagerRelationships(Long id);

    Flux<Medic> findAllWithEagerRelationships();

    Flux<Medic> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
