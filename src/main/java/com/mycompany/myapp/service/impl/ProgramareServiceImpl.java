package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ProgramareRepository;
import com.mycompany.myapp.service.ProgramareService;
import com.mycompany.myapp.service.dto.ProgramareDTO;
import com.mycompany.myapp.service.mapper.ProgramareMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Programare}.
 */
@Service
@Transactional
public class ProgramareServiceImpl implements ProgramareService {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramareServiceImpl.class);

    private final ProgramareRepository programareRepository;

    private final ProgramareMapper programareMapper;

    private final com.mycompany.myapp.service.WaitlistService waitlistService;

    public ProgramareServiceImpl(
        ProgramareRepository programareRepository,
        ProgramareMapper programareMapper,
        com.mycompany.myapp.service.WaitlistService waitlistService
    ) {
        this.programareRepository = programareRepository;
        this.programareMapper = programareMapper;
        this.waitlistService = waitlistService;
    }

    @Override
    public Mono<ProgramareDTO> save(ProgramareDTO programareDTO) {
        LOG.debug("Request to save Programare : {}", programareDTO);
        return programareRepository.save(programareMapper.toEntity(programareDTO)).map(programareMapper::toDto);
    }

    @Override
    public Mono<ProgramareDTO> update(ProgramareDTO programareDTO) {
        LOG.debug("Request to update Programare : {}", programareDTO);
        return programareRepository
            .save(programareMapper.toEntity(programareDTO))
            .flatMap(saved -> {
                if (com.mycompany.myapp.domain.enumeration.ProgramareStatus.ANULATA.equals(saved.getStatus())) {
                    LOG.info(
                        "WaitlistTrigger: TRIGGERED for Appointment {} (Status: ANULATA). Medic ID: {}, Date: {}",
                        saved.getId(),
                        saved.getMedicId(),
                        saved.getDataProgramare()
                    );
                    return waitlistService
                        .checkWaitlistOnCancellation(saved.getMedicId(), saved.getDataProgramare())
                        .then(Mono.just(saved));
                }
                return Mono.just(saved);
            })
            .map(programareMapper::toDto);
    }

    @Override
    public Mono<ProgramareDTO> partialUpdate(ProgramareDTO programareDTO) {
        LOG.debug("Request to partially update Programare : {}", programareDTO);

        return programareRepository
            .findById(programareDTO.getId())
            .map(existingProgramare -> {
                programareMapper.partialUpdate(existingProgramare, programareDTO);

                return existingProgramare;
            })
            .flatMap(programareRepository::save)
            .flatMap(saved -> {
                if (com.mycompany.myapp.domain.enumeration.ProgramareStatus.ANULATA.equals(saved.getStatus())) {
                    LOG.info(
                        "WaitlistTrigger: TRIGGERED for Appointment {} (Partial Update to ANULATA). Medic ID: {}, Date: {}",
                        saved.getId(),
                        saved.getMedicId(),
                        saved.getDataProgramare()
                    );
                    return waitlistService
                        .checkWaitlistOnCancellation(saved.getMedicId(), saved.getDataProgramare())
                        .then(Mono.just(saved));
                }
                return Mono.just(saved);
            })
            .map(programareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Programares");
        return programareRepository.findAllBy(pageable).map(programareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findAllByMedicUserLogin(
        String login,
        java.time.Instant fromDate,
        java.time.Instant toDate,
        Pageable pageable
    ) {
        LOG.debug("Request to get all Programares for Medic user login {}", login);
        return programareRepository.findAllByMedicUserLogin(login, fromDate, toDate, pageable).map(programareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findByMedicIdAndLocatieIdAndDataProgramareBetween(
        Long medicId,
        Long locatieId,
        java.time.Instant fromDate,
        java.time.Instant toDate
    ) {
        LOG.debug(
            "Request to get Programares by medicId: {}, locatieId: {}, fromDate: {}, toDate: {}",
            medicId,
            locatieId,
            fromDate,
            toDate
        );
        return programareRepository
            .findByMedicIdAndLocatieIdAndDataProgramareBetween(medicId, locatieId, fromDate, toDate)
            .map(programareMapper::toDto);
    }

    /**
     *  Get all the programares where FisaMedicala is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findAllWhereFisaMedicalaIsNull() {
        LOG.debug("Request to get all programares where FisaMedicala is null");
        return programareRepository.findAllWhereFisaMedicalaIsNull().map(programareMapper::toDto);
    }

    /**
     *  Get all the programares where RaportProgramare is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findAllWhereRaportProgramareIsNull() {
        LOG.debug("Request to get all programares where RaportProgramare is null");
        return programareRepository.findAllWhereRaportProgramareIsNull().map(programareMapper::toDto);
    }

    public Mono<Long> countAll() {
        return programareRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate) {
        return programareRepository.countAllByMedicUserLogin(login, fromDate, toDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findAllByPacientId(Long pacientId, Pageable pageable) {
        LOG.debug("Request to get all Programares for Pacient : {}", pacientId);
        return programareRepository.findAllByPacientId(pacientId, pageable).map(programareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAllByPacientId(Long pacientId) {
        return programareRepository.countAllByPacientId(pacientId);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramareDTO> findAllByPacientUserLogin(String login, Pageable pageable) {
        LOG.debug("Request to get all Programares for Pacient user login {}", login);
        return programareRepository.findAllByPacientUserLogin(login, pageable).map(programareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countAllByPacientUserLogin(String login) {
        LOG.debug("Request to count all Programares for Pacient user login {}", login);
        return programareRepository.countAllByPacientUserLogin(login);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProgramareDTO> findOne(Long id) {
        LOG.debug("Request to get Programare : {}", id);
        return programareRepository.findById(id).map(programareMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Programare : {}", id);
        return programareRepository
            .findById(id)
            .flatMap(existing -> {
                LOG.info(
                    "WaitlistTrigger: TRIGGERED for Appointment {} (DELETED). Medic ID: {}, Date: {}",
                    id,
                    existing.getMedicId(),
                    existing.getDataProgramare()
                );
                return programareRepository
                    .deleteById(id)
                    .then(waitlistService.checkWaitlistOnCancellation(existing.getMedicId(), existing.getDataProgramare()).then());
            })
            .switchIfEmpty(programareRepository.deleteById(id));
    }
}
