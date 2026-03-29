package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.LocatieRepository;
import com.mycompany.myapp.service.LocatieService;
import com.mycompany.myapp.service.dto.LocatieDTO;
import com.mycompany.myapp.service.mapper.LocatieMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Locatie}.
 */
@Service
@Transactional
public class LocatieServiceImpl implements LocatieService {

    private static final Logger LOG = LoggerFactory.getLogger(LocatieServiceImpl.class);

    private final LocatieRepository locatieRepository;

    private final LocatieMapper locatieMapper;

    public LocatieServiceImpl(LocatieRepository locatieRepository, LocatieMapper locatieMapper) {
        this.locatieRepository = locatieRepository;
        this.locatieMapper = locatieMapper;
    }

    @Override
    public Mono<LocatieDTO> save(LocatieDTO locatieDTO) {
        LOG.debug("Request to save Locatie : {}", locatieDTO);
        return locatieRepository.save(locatieMapper.toEntity(locatieDTO)).map(locatieMapper::toDto);
    }

    @Override
    public Mono<LocatieDTO> update(LocatieDTO locatieDTO) {
        LOG.debug("Request to update Locatie : {}", locatieDTO);
        return locatieRepository.save(locatieMapper.toEntity(locatieDTO)).map(locatieMapper::toDto);
    }

    @Override
    public Mono<LocatieDTO> partialUpdate(LocatieDTO locatieDTO) {
        LOG.debug("Request to partially update Locatie : {}", locatieDTO);

        return locatieRepository
            .findById(locatieDTO.getId())
            .map(existingLocatie -> {
                locatieMapper.partialUpdate(existingLocatie, locatieDTO);

                return existingLocatie;
            })
            .flatMap(locatieRepository::save)
            .map(locatieMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LocatieDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Locaties");
        return locatieRepository.findAllBy(pageable).map(locatieMapper::toDto);
    }

    public Mono<Long> countAll() {
        return locatieRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LocatieDTO> findOne(Long id) {
        LOG.debug("Request to get Locatie : {}", id);
        return locatieRepository.findById(id).map(locatieMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Locatie : {}", id);
        return locatieRepository.deleteById(id);
    }
}
