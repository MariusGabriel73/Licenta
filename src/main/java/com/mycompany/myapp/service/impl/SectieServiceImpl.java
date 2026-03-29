package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.SectieRepository;
import com.mycompany.myapp.service.SectieService;
import com.mycompany.myapp.service.dto.SectieDTO;
import com.mycompany.myapp.service.mapper.SectieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Sectie}.
 */
@Service
@Transactional
public class SectieServiceImpl implements SectieService {

    private static final Logger LOG = LoggerFactory.getLogger(SectieServiceImpl.class);

    private final SectieRepository sectieRepository;

    private final SectieMapper sectieMapper;

    public SectieServiceImpl(SectieRepository sectieRepository, SectieMapper sectieMapper) {
        this.sectieRepository = sectieRepository;
        this.sectieMapper = sectieMapper;
    }

    @Override
    public Mono<SectieDTO> save(SectieDTO sectieDTO) {
        LOG.debug("Request to save Sectie : {}", sectieDTO);
        return sectieRepository.save(sectieMapper.toEntity(sectieDTO)).map(sectieMapper::toDto);
    }

    @Override
    public Mono<SectieDTO> update(SectieDTO sectieDTO) {
        LOG.debug("Request to update Sectie : {}", sectieDTO);
        return sectieRepository.save(sectieMapper.toEntity(sectieDTO)).map(sectieMapper::toDto);
    }

    @Override
    public Mono<SectieDTO> partialUpdate(SectieDTO sectieDTO) {
        LOG.debug("Request to partially update Sectie : {}", sectieDTO);

        return sectieRepository
            .findById(sectieDTO.getId())
            .map(existingSectie -> {
                sectieMapper.partialUpdate(existingSectie, sectieDTO);

                return existingSectie;
            })
            .flatMap(sectieRepository::save)
            .map(sectieMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SectieDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Secties");
        return sectieRepository.findAllBy(pageable).map(sectieMapper::toDto);
    }

    public Mono<Long> countAll() {
        return sectieRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SectieDTO> findOne(Long id) {
        LOG.debug("Request to get Sectie : {}", id);
        return sectieRepository.findById(id).map(sectieMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Sectie : {}", id);
        return sectieRepository.deleteById(id);
    }
}
