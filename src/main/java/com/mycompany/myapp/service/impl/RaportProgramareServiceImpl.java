package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.RaportProgramareRepository;
import com.mycompany.myapp.service.RaportProgramareService;
import com.mycompany.myapp.service.dto.RaportProgramareDTO;
import com.mycompany.myapp.service.mapper.RaportProgramareMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.RaportProgramare}.
 */
@Service
@Transactional
public class RaportProgramareServiceImpl implements RaportProgramareService {

    private static final Logger LOG = LoggerFactory.getLogger(RaportProgramareServiceImpl.class);

    private final RaportProgramareRepository raportProgramareRepository;

    private final RaportProgramareMapper raportProgramareMapper;

    public RaportProgramareServiceImpl(
        RaportProgramareRepository raportProgramareRepository,
        RaportProgramareMapper raportProgramareMapper
    ) {
        this.raportProgramareRepository = raportProgramareRepository;
        this.raportProgramareMapper = raportProgramareMapper;
    }

    @Override
    public Mono<RaportProgramareDTO> save(RaportProgramareDTO raportProgramareDTO) {
        LOG.debug("Request to save RaportProgramare : {}", raportProgramareDTO);
        return raportProgramareRepository.save(raportProgramareMapper.toEntity(raportProgramareDTO)).map(raportProgramareMapper::toDto);
    }

    @Override
    public Mono<RaportProgramareDTO> update(RaportProgramareDTO raportProgramareDTO) {
        LOG.debug("Request to update RaportProgramare : {}", raportProgramareDTO);
        return raportProgramareRepository.save(raportProgramareMapper.toEntity(raportProgramareDTO)).map(raportProgramareMapper::toDto);
    }

    @Override
    public Mono<RaportProgramareDTO> partialUpdate(RaportProgramareDTO raportProgramareDTO) {
        LOG.debug("Request to partially update RaportProgramare : {}", raportProgramareDTO);

        return raportProgramareRepository
            .findById(raportProgramareDTO.getId())
            .map(existingRaportProgramare -> {
                raportProgramareMapper.partialUpdate(existingRaportProgramare, raportProgramareDTO);

                return existingRaportProgramare;
            })
            .flatMap(raportProgramareRepository::save)
            .map(raportProgramareMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RaportProgramareDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all RaportProgramares");
        return raportProgramareRepository.findAllBy(pageable).map(raportProgramareMapper::toDto);
    }

    public Mono<Long> countAll() {
        return raportProgramareRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RaportProgramareDTO> findOne(Long id) {
        LOG.debug("Request to get RaportProgramare : {}", id);
        return raportProgramareRepository.findById(id).map(raportProgramareMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete RaportProgramare : {}", id);
        return raportProgramareRepository.deleteById(id);
    }
}
