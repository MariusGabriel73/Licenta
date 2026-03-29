package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.SpecializareRepository;
import com.mycompany.myapp.service.SpecializareService;
import com.mycompany.myapp.service.dto.SpecializareDTO;
import com.mycompany.myapp.service.mapper.SpecializareMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Specializare}.
 */
@Service
@Transactional
public class SpecializareServiceImpl implements SpecializareService {

    private static final Logger LOG = LoggerFactory.getLogger(SpecializareServiceImpl.class);

    private final SpecializareRepository specializareRepository;

    private final SpecializareMapper specializareMapper;

    public SpecializareServiceImpl(SpecializareRepository specializareRepository, SpecializareMapper specializareMapper) {
        this.specializareRepository = specializareRepository;
        this.specializareMapper = specializareMapper;
    }

    @Override
    public Mono<SpecializareDTO> save(SpecializareDTO specializareDTO) {
        LOG.debug("Request to save Specializare : {}", specializareDTO);
        return specializareRepository.save(specializareMapper.toEntity(specializareDTO)).map(specializareMapper::toDto);
    }

    @Override
    public Mono<SpecializareDTO> update(SpecializareDTO specializareDTO) {
        LOG.debug("Request to update Specializare : {}", specializareDTO);
        return specializareRepository.save(specializareMapper.toEntity(specializareDTO)).map(specializareMapper::toDto);
    }

    @Override
    public Mono<SpecializareDTO> partialUpdate(SpecializareDTO specializareDTO) {
        LOG.debug("Request to partially update Specializare : {}", specializareDTO);

        return specializareRepository
            .findById(specializareDTO.getId())
            .map(existingSpecializare -> {
                specializareMapper.partialUpdate(existingSpecializare, specializareDTO);

                return existingSpecializare;
            })
            .flatMap(specializareRepository::save)
            .map(specializareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SpecializareDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Specializares");
        return specializareRepository.findAllBy(pageable).map(specializareMapper::toDto);
    }

    public Mono<Long> countAll() {
        return specializareRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SpecializareDTO> findOne(Long id) {
        LOG.debug("Request to get Specializare : {}", id);
        return specializareRepository.findById(id).map(specializareMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Specializare : {}", id);
        return specializareRepository.deleteById(id);
    }
}
