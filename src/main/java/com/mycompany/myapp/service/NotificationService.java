package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Pacient;
import com.mycompany.myapp.repository.PacientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service for simulated SMS/Email notifications.
 */
@Service
public class NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final PacientRepository pacientRepository;

    public NotificationService(PacientRepository pacientRepository) {
        this.pacientRepository = pacientRepository;
    }

    public Mono<Void> notifyMatchFound(Long pacientId, String medicName, String dateStr) {
        return pacientRepository
            .findById(pacientId)
            .flatMap(pacient -> {
                String contactInfo = pacient.getTelefon() != null ? pacient.getTelefon() : "Email general";

                LOG.info("****************************************************************");
                LOG.info("🚀 [SMART WAITLIST] NOTIF_SENT to Pacient ID: {} ({})", pacientId, contactInfo);
                LOG.info("📅 Detalii: Un loc s-a eliberat pentru Dr. {} în data de {}", medicName, dateStr);
                LOG.info("🔗 Acțiune: Pacientul poate confirma locul acum din aplicație.");
                LOG.info("****************************************************************");

                return Mono.empty();
            })
            .then();
    }
}
