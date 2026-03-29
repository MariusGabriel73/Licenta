package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ProgramRepository;
import com.mycompany.myapp.service.ProgramService;
import com.mycompany.myapp.service.dto.ProgramDTO;
import com.mycompany.myapp.service.mapper.ProgramMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Program}.
 */
@Service
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramServiceImpl.class);

    private final ProgramRepository programRepository;

    private final ProgramMapper programMapper;

    public ProgramServiceImpl(ProgramRepository programRepository, ProgramMapper programMapper) {
        this.programRepository = programRepository;
        this.programMapper = programMapper;
    }

    @Override
    public Mono<ProgramDTO> save(ProgramDTO programDTO) {
        LOG.debug("Request to save Program : {}", programDTO);
        return programRepository.save(programMapper.toEntity(programDTO)).map(programMapper::toDto);
    }

    @Override
    public Mono<ProgramDTO> update(ProgramDTO programDTO) {
        LOG.debug("Request to update Program : {}", programDTO);
        return programRepository.save(programMapper.toEntity(programDTO)).map(programMapper::toDto);
    }

    @Override
    public Mono<ProgramDTO> partialUpdate(ProgramDTO programDTO) {
        LOG.debug("Request to partially update Program : {}", programDTO);

        return programRepository
            .findById(programDTO.getId())
            .map(existingProgram -> {
                programMapper.partialUpdate(existingProgram, programDTO);

                return existingProgram;
            })
            .flatMap(programRepository::save)
            .map(programMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Programs");
        return programRepository.findAllBy(pageable).map(programMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProgramDTO> findByMedicAndLocatie(Long medicId, Long locatieId) {
        LOG.debug("Request to get Programs by medicId: {} and locatieId: {}", medicId, locatieId);
        return programRepository.findByMedicAndLocatie(medicId, locatieId).map(programMapper::toDto);
    }

    public Mono<Long> countAll() {
        return programRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProgramDTO> findOne(Long id) {
        LOG.debug("Request to get Program : {}", id);
        return programRepository.findById(id).map(programMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Program : {}", id);
        return programRepository.deleteById(id);
    }
}
