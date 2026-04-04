package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.FisaMedicala;
import com.mycompany.myapp.domain.MedicationDose;
import com.mycompany.myapp.repository.MedicationDoseRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing medication doses and compliance.
 */
@Service
@Transactional
public class MedicationDoseService {

    private static final Logger LOG = LoggerFactory.getLogger(MedicationDoseService.class);

    // Pattern 1: [Medicament] la [X] ore [D] zile
    private static final Pattern PATTERN_HOURS = Pattern.compile(
        "(?i)(?<name>.+?)\\s+la\\s+(?<hours>\\d+)\\s+ore\\s+(?:timp\\s+de\\s+)?(?<days>\\d+)\\s+zile"
    );

    // Pattern 2: [Medicament] [X] [cuvânt opțional] pe zi/ori pe zi [D] zile
    private static final Pattern PATTERN_PER_DAY = Pattern.compile(
        "(?i)(?<name>.+?)(?:\\s+|,)\\s*(?<times>\\d+)(?:\\s+\\w+)?\\s*(?:pe|/|ori\\s+pe)\\s*zi\\s+(?:timp\\s+de\\s+)?(?<days>\\d+)\\s+zile"
    );

    private final MedicationDoseRepository medicationDoseRepository;

    public MedicationDoseService(MedicationDoseRepository medicationDoseRepository) {
        this.medicationDoseRepository = medicationDoseRepository;
    }

    public Flux<MedicationDose> generateDosesForFisa(Long fisaId, String tratamentText) {
        LOG.debug("Generating doses for FisaMedicala {} with treatment: {}", fisaId, tratamentText);

        return medicationDoseRepository
            .deleteAllByFisaMedicalaId(fisaId)
            .thenMany(
                Flux.defer(() -> {
                    if (tratamentText == null || tratamentText.isBlank()) {
                        return Flux.empty();
                    }

                    List<MedicationDose> doses = new ArrayList<>();
                    String[] lines = tratamentText.split("\\r?\\n");

                    for (String line : lines) {
                        if (line.isBlank()) continue;

                        boolean matched = false;

                        // Try "la X ore" pattern
                        Matcher mHours = PATTERN_HOURS.matcher(line);
                        if (mHours.find()) {
                            matched = true;
                            addDoses(
                                doses,
                                fisaId,
                                mHours.group("name").trim(),
                                Integer.parseInt(mHours.group("hours")),
                                Integer.parseInt(mHours.group("days"))
                            );
                        }

                        // Try "X pe zi" pattern if not already matched
                        if (!matched) {
                            Matcher mPerDay = PATTERN_PER_DAY.matcher(line);
                            if (mPerDay.find()) {
                                int times = Integer.parseInt(mPerDay.group("times"));
                                int hours = (times <= 0) ? 24 : Math.max(1, 24 / times);
                                addDoses(doses, fisaId, mPerDay.group("name").trim(), hours, Integer.parseInt(mPerDay.group("days")));
                            }
                        }
                    }

                    if (doses.isEmpty()) {
                        LOG.debug("No treatment patterns found in text: {}", tratamentText);
                        return Flux.empty();
                    }

                    return medicationDoseRepository.saveAll(doses);
                })
            );
    }

    private void addDoses(List<MedicationDose> doses, Long fisaId, String name, int hours, int days) {
        ZonedDateTime now = ZonedDateTime.now();
        int totalDoses = (days * 24) / hours;

        LOG.debug("Detected prescription: {} every {} hours for {} days. Total doses: {}", name, hours, days, totalDoses);

        for (int i = 0; i < totalDoses; i++) {
            MedicationDose dose = new MedicationDose();
            dose.setFisaMedicalaId(fisaId);
            dose.setMedicament(name);
            dose.setOraPlanificata(now.plusHours((long) i * hours));
            dose.setStatus("PENDING");
            doses.add(dose);
        }
    }

    public Mono<MedicationDose> confirmDose(Long doseId) {
        return medicationDoseRepository
            .findById(doseId)
            .flatMap(dose -> {
                dose.setOraConfirmata(ZonedDateTime.now());
                dose.setStatus("TAKEN");
                return medicationDoseRepository.save(dose);
            });
    }

    public Flux<MedicationDose> getDosesByFisa(Long fisaId) {
        return medicationDoseRepository.findAllByFisaMedicalaIdOrderByOraPlanificataAsc(fisaId);
    }

    public Flux<MedicationDose> getDosesByPacient(Long pacientId) {
        return medicationDoseRepository.findAllByPacientId(pacientId);
    }
}
