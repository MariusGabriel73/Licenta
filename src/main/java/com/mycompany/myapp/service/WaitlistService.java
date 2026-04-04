package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Waitlist;
import com.mycompany.myapp.repository.MedicRepository;
import com.mycompany.myapp.repository.PacientRepository;
import com.mycompany.myapp.repository.ProgramareRepository;
import com.mycompany.myapp.repository.WaitlistRepository;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing Waitlist.
 */
@Service
@Transactional
public class WaitlistService {

    private static final Logger LOG = LoggerFactory.getLogger(WaitlistService.class);

    private final WaitlistRepository waitlistRepository;
    private final NotificationService notificationService;
    private final MedicRepository medicRepository;
    private final MailService mailService;
    private final PacientRepository pacientRepository;
    private final ProgramareRepository programareRepository;

    public WaitlistService(
        WaitlistRepository waitlistRepository,
        NotificationService notificationService,
        MedicRepository medicRepository,
        MailService mailService,
        PacientRepository pacientRepository,
        ProgramareRepository programareRepository
    ) {
        this.waitlistRepository = waitlistRepository;
        this.notificationService = notificationService;
        this.medicRepository = medicRepository;
        this.mailService = mailService;
        this.pacientRepository = pacientRepository;
        this.programareRepository = programareRepository;
    }

    public Mono<Waitlist> addToWaitlist(Waitlist waitlist) {
        LOG.debug("Request to add to Waitlist : {}", waitlist);
        waitlist.setCreatedAt(ZonedDateTime.now());
        waitlist.setStatus("WAITING");
        return waitlistRepository.save(waitlist);
    }

    /**
     * Claim a spot from the waitlist and create an appointment.
     */
    public Mono<com.mycompany.myapp.domain.Programare> claimWaitlistSpot(Long waitlistId) {
        LOG.debug("Claiming waitlist spot ID: {}", waitlistId);
        return waitlistRepository
            .findById(waitlistId)
            .flatMap(waitlist -> {
                com.mycompany.myapp.domain.Programare programare = new com.mycompany.myapp.domain.Programare();
                // Preferăm ora exactă a slotului eliberat (notifiedSlotTime), cu fallback la dataPreferata
                java.time.ZonedDateTime appointmentTime = waitlist.getNotifiedSlotTime() != null
                    ? waitlist.getNotifiedSlotTime()
                    : waitlist.getDataPreferata();
                programare.setDataProgramare(appointmentTime);
                programare.setMedicId(waitlist.getMedicId());
                programare.setPacientId(waitlist.getPacientId());
                programare.setClinicaId(waitlist.getClinicaId());
                programare.setStatus(com.mycompany.myapp.domain.enumeration.ProgramareStatus.ACTIVA);
                programare.setObservatii("Rezervat automat prin Smart Waitlist");

                waitlist.setStatus("ACCEPTED");

                return programareRepository.save(programare).flatMap(savedAppt -> waitlistRepository.save(waitlist).thenReturn(savedAppt));
            });
    }

    /**
     * Triggered on appointment cancellation.
     * Finds the first compatible patient and sends a notification.
     */
    public Flux<Waitlist> checkWaitlistOnCancellation(Long medicId, ZonedDateTime date) {
        LOG.info("WaitlistTrigger: START checking for Medic {} on slot date {}", medicId, date);

        // Use a lenient ±1 day range to handle any timezone offset stored in data_preferata
        // e.g., if waitlist was saved as 2026-04-30T08:00:00+03:00 = 2026-04-30T05:00:00Z
        // and appointment was at 2026-04-30T08:00:00+03:00, truncated to day = 2026-04-30T00:00:00Z
        // Without leniency at midnight edges this might miss. With ±1 day it's always safe.
        ZonedDateTime startOfSearch = date.truncatedTo(java.time.temporal.ChronoUnit.DAYS).minusDays(1);
        ZonedDateTime endOfSearch = startOfSearch.plusDays(3); // covers full ±1 day range

        LOG.info("WaitlistTrigger: Searching for WAITING entries between {} and {}", startOfSearch, endOfSearch);

        return waitlistRepository
            .findFirstWaitingForMedicAndDate(medicId, startOfSearch, endOfSearch)
            .collectList()
            .flatMapMany(entries -> {
                if (entries.isEmpty()) {
                    LOG.info("WaitlistTrigger: No WAITING entries found for Medic {} on {}. Trigger END.", medicId, date.toLocalDate());
                    return Flux.empty();
                }

                Waitlist entry = entries.get(0);
                LOG.info(
                    "WaitlistTrigger: Found MATCHING entry ID: {} for Pacient ID: {}. Processing notification...",
                    entry.getId(),
                    entry.getPacientId()
                );

                return medicRepository
                    .findOneWithEagerRelationships(medicId)
                    .flatMapMany(medic -> {
                        String medicName = (medic != null && medic.getUser() != null)
                            ? medic.getUser().getLastName() + " " + medic.getUser().getFirstName()
                            : "Doctor #" + medicId;

                        // Convert to Romanian timezone for display (UTC+3 in summer, UTC+2 in winter)
                        java.time.ZoneId romanianZone = java.time.ZoneId.of("Europe/Bucharest");
                        String dateStr = date
                            .withZoneSameInstant(romanianZone)
                            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                        return pacientRepository
                            .findById(entry.getPacientId())
                            .flatMap(pacient -> {
                                if (pacient != null && pacient.getUser() != null) {
                                    LOG.info(
                                        "WaitlistTrigger: Sending EMAIL to user {} ({})",
                                        pacient.getUser().getLogin(),
                                        pacient.getUser().getEmail()
                                    );
                                    mailService.sendWaitlistNotificationEmail(pacient.getUser(), medicName, dateStr);
                                } else {
                                    LOG.warn(
                                        "WaitlistTrigger: Could NOT send email. Pacient ID {} has no associated User object.",
                                        entry.getPacientId()
                                    );
                                }

                                LOG.info("WaitlistTrigger: Sending simulated IN-APP alert to Pacient ID: {}", entry.getPacientId());
                                return notificationService.notifyMatchFound(entry.getPacientId(), medicName, dateStr);
                            })
                            .then(
                                Mono.defer(() -> {
                                    LOG.info(
                                        "WaitlistTrigger: Updating entry ID: {} status to NOTIFIED, slot time: {}",
                                        entry.getId(),
                                        date
                                    );
                                    entry.setStatus("NOTIFIED");
                                    entry.setNotifiedSlotTime(date);
                                    return waitlistRepository.save(entry);
                                })
                            )
                            .flux();
                    });
            });
    }

    public Flux<Waitlist> findMyWaitlistEntries(Long pacientId) {
        return waitlistRepository.findAllByPacientIdOrderByCreatedAtDesc(pacientId);
    }
}
