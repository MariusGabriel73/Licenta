package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MedicationDose;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the MedicationDose entity.
 */
@Repository
public interface MedicationDoseRepository extends ReactiveCrudRepository<MedicationDose, Long> {
    Flux<MedicationDose> findAllByFisaMedicalaIdOrderByOraPlanificataAsc(Long fisaMedicalaId);

    Flux<MedicationDose> findAllByFisaMedicalaIdAndStatus(Long fisaMedicalaId, String status);

    @Query(
        "SELECT md.* FROM medication_dose md " +
        "JOIN fisa_medicala fm ON md.fisa_medicala_id = fm.id " +
        "JOIN programare p ON fm.programare_id = p.id " +
        "WHERE p.pacient_id = :pacientId " +
        "ORDER BY md.ora_planificata ASC"
    )
    Flux<MedicationDose> findAllByPacientId(Long pacientId);

    Mono<Void> deleteAllByFisaMedicalaId(Long fisaMedicalaId);
}
