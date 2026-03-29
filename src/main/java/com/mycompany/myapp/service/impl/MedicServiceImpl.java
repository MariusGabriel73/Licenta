package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.MedicRepository;
import com.mycompany.myapp.service.MedicService;
import com.mycompany.myapp.service.dto.MedicDTO;
import com.mycompany.myapp.service.mapper.MedicMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Medic}.
 */
@Service
@Transactional
public class MedicServiceImpl implements MedicService {

    private static final Logger LOG = LoggerFactory.getLogger(MedicServiceImpl.class);

    private final MedicRepository medicRepository;

    private final MedicMapper medicMapper;

    public MedicServiceImpl(MedicRepository medicRepository, MedicMapper medicMapper) {
        this.medicRepository = medicRepository;
        this.medicMapper = medicMapper;
    }

    @Override
    public Mono<MedicDTO> save(MedicDTO medicDTO) {
        LOG.debug("Request to save Medic : {}", medicDTO);
        return medicRepository.save(medicMapper.toEntity(medicDTO)).map(medicMapper::toDto);
    }

    @Override
    public Mono<MedicDTO> update(MedicDTO medicDTO) {
        LOG.debug("Request to update Medic : {}", medicDTO);
        return medicRepository.save(medicMapper.toEntity(medicDTO)).map(medicMapper::toDto);
    }

    @Override
    public Mono<MedicDTO> partialUpdate(MedicDTO medicDTO) {
        LOG.debug("Request to partially update Medic : {}", medicDTO);

        return medicRepository
            .findById(medicDTO.getId())
            .map(existingMedic -> {
                medicMapper.partialUpdate(existingMedic, medicDTO);

                return existingMedic;
            })
            .flatMap(medicRepository::save)
            .map(medicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MedicDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Medics");
        return medicRepository.findAllBy(pageable).map(medicMapper::toDto);
    }

    public Flux<MedicDTO> findAllWithEagerRelationships(Pageable pageable) {
        return medicRepository.findAllWithEagerRelationships(pageable).map(medicMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<MedicDTO> findByCascadeFilter(Long clinicaId, Long sectieId) {
        LOG.debug("Request to get Medics by clinicaId={}, sectieId={}", clinicaId, sectieId);
        return medicRepository.findByCascadeFilter(clinicaId, sectieId).map(medicMapper::toDto);
    }

    public Mono<Long> countAll() {
        return medicRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<MedicDTO> findOne(Long id) {
        LOG.debug("Request to get Medic : {}", id);
        return medicRepository.findOneWithEagerRelationships(id).map(medicMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Medic : {}", id);
        return medicRepository.deleteById(id);
    }
}
