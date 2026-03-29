package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.PacientRepository;
import com.mycompany.myapp.service.PacientService;
import com.mycompany.myapp.service.dto.PacientDTO;
import com.mycompany.myapp.service.mapper.PacientMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Pacient}.
 */
@Service
@Transactional
public class PacientServiceImpl implements PacientService {

    private static final Logger LOG = LoggerFactory.getLogger(PacientServiceImpl.class);

    private final PacientRepository pacientRepository;

    private final PacientMapper pacientMapper;

    public PacientServiceImpl(PacientRepository pacientRepository, PacientMapper pacientMapper) {
        this.pacientRepository = pacientRepository;
        this.pacientMapper = pacientMapper;
    }

    @Override
    public Mono<PacientDTO> save(PacientDTO pacientDTO) {
        LOG.debug("Request to save Pacient : {}", pacientDTO);
        return pacientRepository.save(pacientMapper.toEntity(pacientDTO)).map(pacientMapper::toDto);
    }

    @Override
    public Mono<PacientDTO> update(PacientDTO pacientDTO) {
        LOG.debug("Request to update Pacient : {}", pacientDTO);
        return pacientRepository.save(pacientMapper.toEntity(pacientDTO)).map(pacientMapper::toDto);
    }

    @Override
    public Mono<PacientDTO> partialUpdate(PacientDTO pacientDTO) {
        LOG.debug("Request to partially update Pacient : {}", pacientDTO);

        return pacientRepository
            .findById(pacientDTO.getId())
            .map(existingPacient -> {
                pacientMapper.partialUpdate(existingPacient, pacientDTO);

                return existingPacient;
            })
            .flatMap(pacientRepository::save)
            .map(pacientMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PacientDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pacients");
        return pacientRepository.findAllBy(pageable).map(pacientMapper::toDto);
    }

    public Mono<Long> countAll() {
        return pacientRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PacientDTO> findOne(Long id) {
        LOG.debug("Request to get Pacient : {}", id);
        return pacientRepository.findById(id).map(pacientMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Pacient : {}", id);
        return pacientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PacientDTO> findByUserLogin(String login) {
        LOG.debug("Request to get Pacient by user login: {}", login);
        return pacientRepository.findByUserLogin(login).map(pacientMapper::toDto);
    }
}
