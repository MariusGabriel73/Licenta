package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.repository.ClinicaRepository;
import com.mycompany.myapp.service.ClinicaService;
import com.mycompany.myapp.service.dto.ClinicaDTO;
import com.mycompany.myapp.service.mapper.ClinicaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Clinica}.
 */
@Service
@Transactional
public class ClinicaServiceImpl implements ClinicaService {

    private static final Logger LOG = LoggerFactory.getLogger(ClinicaServiceImpl.class);

    private final ClinicaRepository clinicaRepository;

    private final ClinicaMapper clinicaMapper;

    public ClinicaServiceImpl(ClinicaRepository clinicaRepository, ClinicaMapper clinicaMapper) {
        this.clinicaRepository = clinicaRepository;
        this.clinicaMapper = clinicaMapper;
    }

    @Override
    public Mono<ClinicaDTO> save(ClinicaDTO clinicaDTO) {
        LOG.debug("Request to save Clinica : {}", clinicaDTO);
        return clinicaRepository.save(clinicaMapper.toEntity(clinicaDTO)).map(clinicaMapper::toDto);
    }

    @Override
    public Mono<ClinicaDTO> update(ClinicaDTO clinicaDTO) {
        LOG.debug("Request to update Clinica : {}", clinicaDTO);
        return clinicaRepository.save(clinicaMapper.toEntity(clinicaDTO)).map(clinicaMapper::toDto);
    }

    @Override
    public Mono<ClinicaDTO> partialUpdate(ClinicaDTO clinicaDTO) {
        LOG.debug("Request to partially update Clinica : {}", clinicaDTO);

        return clinicaRepository
            .findById(clinicaDTO.getId())
            .map(existingClinica -> {
                clinicaMapper.partialUpdate(existingClinica, clinicaDTO);

                return existingClinica;
            })
            .flatMap(clinicaRepository::save)
            .map(clinicaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClinicaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Clinicas");
        return clinicaRepository.findAllBy(pageable).map(clinicaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClinicaDTO> findByLocatieId(Long locatieId) {
        LOG.debug("Request to get Clinicas by locatieId: {}", locatieId);
        return clinicaRepository.findByLocatie(locatieId).map(clinicaMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ClinicaDTO> findByMedicUserLogin(String login) {
        LOG.debug("Request to get Clinicas by medicUserLogin: {}", login);
        return clinicaRepository.findByMedicUserLogin(login).map(clinicaMapper::toDto);
    }

    public Mono<Long> countAll() {
        return clinicaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ClinicaDTO> findOne(Long id) {
        LOG.debug("Request to get Clinica : {}", id);
        return clinicaRepository.findById(id).map(clinicaMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Clinica : {}", id);
        return clinicaRepository.deleteById(id);
    }
}
