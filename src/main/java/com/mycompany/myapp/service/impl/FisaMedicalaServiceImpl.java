package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.FisaMedicalaRepository;
import com.mycompany.myapp.service.FisaMedicalaService;
import com.mycompany.myapp.service.dto.FisaMedicalaDTO;
import com.mycompany.myapp.service.mapper.FisaMedicalaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.FisaMedicala}.
 */
@Service
@Transactional
public class FisaMedicalaServiceImpl implements FisaMedicalaService {

    private static final Logger LOG = LoggerFactory.getLogger(FisaMedicalaServiceImpl.class);

    private final FisaMedicalaRepository fisaMedicalaRepository;

    private final FisaMedicalaMapper fisaMedicalaMapper;

    public FisaMedicalaServiceImpl(FisaMedicalaRepository fisaMedicalaRepository, FisaMedicalaMapper fisaMedicalaMapper) {
        this.fisaMedicalaRepository = fisaMedicalaRepository;
        this.fisaMedicalaMapper = fisaMedicalaMapper;
    }

    @Override
    public Mono<FisaMedicalaDTO> save(FisaMedicalaDTO fisaMedicalaDTO) {
        LOG.debug("Request to save FisaMedicala : {}", fisaMedicalaDTO);
        return fisaMedicalaRepository.save(fisaMedicalaMapper.toEntity(fisaMedicalaDTO)).map(fisaMedicalaMapper::toDto);
    }

    @Override
    public Mono<FisaMedicalaDTO> update(FisaMedicalaDTO fisaMedicalaDTO) {
        LOG.debug("Request to update FisaMedicala : {}", fisaMedicalaDTO);
        return fisaMedicalaRepository.save(fisaMedicalaMapper.toEntity(fisaMedicalaDTO)).map(fisaMedicalaMapper::toDto);
    }

    @Override
    public Mono<FisaMedicalaDTO> partialUpdate(FisaMedicalaDTO fisaMedicalaDTO) {
        LOG.debug("Request to partially update FisaMedicala : {}", fisaMedicalaDTO);

        return fisaMedicalaRepository
            .findById(fisaMedicalaDTO.getId())
            .map(existingFisaMedicala -> {
                fisaMedicalaMapper.partialUpdate(existingFisaMedicala, fisaMedicalaDTO);

                return existingFisaMedicala;
            })
            .flatMap(fisaMedicalaRepository::save)
            .map(fisaMedicalaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<FisaMedicalaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FisaMedicalas");
        return fisaMedicalaRepository.findAllBy(pageable).map(fisaMedicalaMapper::toDto);
    }

    public Mono<Long> countAll() {
        return fisaMedicalaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<FisaMedicalaDTO> findOne(Long id) {
        LOG.debug("Request to get FisaMedicala : {}", id);
        return fisaMedicalaRepository.findById(id).map(fisaMedicalaMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete FisaMedicala : {}", id);
        return fisaMedicalaRepository.deleteById(id);
    }
}
