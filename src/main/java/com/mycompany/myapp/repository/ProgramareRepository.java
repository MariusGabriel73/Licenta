package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Programare;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Programare entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProgramareRepository extends ReactiveCrudRepository<Programare, Long>, ProgramareRepositoryInternal {
    Flux<Programare> findAllBy(Pageable pageable);

    @Query("SELECT * FROM programare entity WHERE entity.pacient_id IS NULL")
    Flux<Programare> findAllWherePacientIsNull();

    @Query("SELECT * FROM programare entity WHERE entity.medic_id = :id")
    Flux<Programare> findByMedic(Long id);

    @Query("SELECT * FROM programare entity WHERE entity.medic_id IS NULL")
    Flux<Programare> findAllWhereMedicIsNull();

    @Query("SELECT * FROM programare entity WHERE entity.clinica_id = :id")
    Flux<Programare> findByClinica(Long id);

    @Query("SELECT * FROM programare entity WHERE entity.clinica_id IS NULL")
    Flux<Programare> findAllWhereClinicaIsNull();

    @Query("SELECT * FROM programare entity WHERE entity.id not in (select programare_id from fisa_medicala)")
    Flux<Programare> findAllWhereFisaMedicalaIsNull();

    @Query("SELECT * FROM programare entity WHERE entity.id not in (select programare_id from raport_programare)")
    Flux<Programare> findAllWhereRaportProgramareIsNull();

    @Override
    <S extends Programare> Mono<S> save(S entity);

    @Override
    Flux<Programare> findAll();

    @Override
    Mono<Programare> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProgramareRepositoryInternal {
    <S extends Programare> Mono<S> save(S entity);

    Flux<Programare> findAllBy(Pageable pageable);

    Flux<Programare> findAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate, Pageable pageable);

    Flux<Programare> findByMedicIdAndLocatieIdAndDataProgramareBetween(
        Long medicId,
        Long locatieId,
        java.time.Instant fromDate,
        java.time.Instant toDate
    );

    Mono<Long> countAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate);

    Flux<Programare> findAllByPacientId(Long pacientId, Pageable pageable);

    Mono<Long> countAllByPacientId(Long pacientId);

    Flux<Programare> findAll();

    Mono<Programare> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Programare> findAllBy(Pageable pageable, Criteria criteria);
}
