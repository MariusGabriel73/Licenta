package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Waitlist;
import java.time.ZonedDateTime;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Spring Data R2DBC repository for the Waitlist entity.
 */
@Repository
public interface WaitlistRepository extends ReactiveCrudRepository<Waitlist, Long> {
    @Query(
        "SELECT * FROM waitlist WHERE medic_id = :medicId AND data_preferata >= :start AND data_preferata < :end AND status = 'WAITING' ORDER BY created_at ASC"
    )
    Flux<Waitlist> findFirstWaitingForMedicAndDate(Long medicId, java.time.ZonedDateTime start, java.time.ZonedDateTime end);

    @Query(
        "SELECT * FROM waitlist WHERE medic_id = :medicId AND clinica_id = :clinicaId AND locatie_id = :locatieId AND data_preferata >= :start AND data_preferata < :end AND status = 'WAITING' ORDER BY created_at ASC"
    )
    Flux<Waitlist> findFirstWaitingForSpecificClinic(
        Long medicId,
        Long clinicaId,
        Long locatieId,
        java.time.ZonedDateTime start,
        java.time.ZonedDateTime end
    );

    Flux<Waitlist> findAllByPacientIdOrderByCreatedAtDesc(Long pacientId);
}
