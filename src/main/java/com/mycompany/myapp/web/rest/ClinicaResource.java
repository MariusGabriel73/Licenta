package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ClinicaRepository;
import com.mycompany.myapp.service.ClinicaService;
import com.mycompany.myapp.service.dto.ClinicaDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Clinica}.
 */
@RestController
@RequestMapping("/api/clinicas")
public class ClinicaResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClinicaResource.class);

    private static final String ENTITY_NAME = "clinica";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClinicaService clinicaService;

    private final ClinicaRepository clinicaRepository;

    public ClinicaResource(ClinicaService clinicaService, ClinicaRepository clinicaRepository) {
        this.clinicaService = clinicaService;
        this.clinicaRepository = clinicaRepository;
    }

    /**
     * {@code POST  /clinicas} : Create a new clinica.
     *
     * @param clinicaDTO the clinicaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clinicaDTO, or with status {@code 400 (Bad Request)} if the clinica has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ClinicaDTO>> createClinica(@Valid @RequestBody ClinicaDTO clinicaDTO) throws URISyntaxException {
        LOG.debug("REST request to save Clinica : {}", clinicaDTO);
        if (clinicaDTO.getId() != null) {
            throw new BadRequestAlertException("A new clinica cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return clinicaService
            .save(clinicaDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/clinicas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /clinicas/:id} : Updates an existing clinica.
     *
     * @param id the id of the clinicaDTO to save.
     * @param clinicaDTO the clinicaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clinicaDTO,
     * or with status {@code 400 (Bad Request)} if the clinicaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clinicaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClinicaDTO>> updateClinica(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ClinicaDTO clinicaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Clinica : {}, {}", id, clinicaDTO);
        if (clinicaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clinicaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clinicaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return clinicaService
                    .update(clinicaDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /clinicas/:id} : Partial updates given fields of an existing clinica, field will ignore if it is null
     *
     * @param id the id of the clinicaDTO to save.
     * @param clinicaDTO the clinicaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clinicaDTO,
     * or with status {@code 400 (Bad Request)} if the clinicaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the clinicaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the clinicaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ClinicaDTO>> partialUpdateClinica(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ClinicaDTO clinicaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Clinica partially : {}, {}", id, clinicaDTO);
        if (clinicaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, clinicaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return clinicaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ClinicaDTO> result = clinicaService.partialUpdate(clinicaDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /clinicas} : get all the clinicas, optionally filtered by locatieId.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param locatieId optional filter — when provided, only returns clinics for that location.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clinicas in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ClinicaDTO>>> getAllClinicas(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(required = false) Long locatieId,
        @RequestParam(required = false) String medicUserLogin
    ) {
        LOG.debug("REST request to get a page of Clinicas");
        return clinicaService
            .countAll()
            .zipWith(clinicaService.findAll(pageable).collectList())
            .flatMap(countWithEntities -> {
                if (locatieId != null) {
                    return clinicaService
                        .findByLocatieId(locatieId)
                        .collectList()
                        .map(list ->
                            ResponseEntity.ok()
                                .headers(
                                    PaginationUtil.generatePaginationHttpHeaders(
                                        org.springframework.web.util.ForwardedHeaderUtils.adaptFromForwardedHeaders(
                                            request.getURI(),
                                            request.getHeaders()
                                        ),
                                        new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                                    )
                                )
                                .body(list)
                        );
                } else if (medicUserLogin != null) {
                    return clinicaService
                        .findByMedicUserLogin(medicUserLogin)
                        .collectList()
                        .map(list ->
                            ResponseEntity.ok()
                                .headers(
                                    PaginationUtil.generatePaginationHttpHeaders(
                                        org.springframework.web.util.ForwardedHeaderUtils.adaptFromForwardedHeaders(
                                            request.getURI(),
                                            request.getHeaders()
                                        ),
                                        new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                                    )
                                )
                                .body(list)
                        );
                }
                return Mono.just(
                    ResponseEntity.ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                org.springframework.web.util.ForwardedHeaderUtils.adaptFromForwardedHeaders(
                                    request.getURI(),
                                    request.getHeaders()
                                ),
                                new org.springframework.data.domain.PageImpl<>(
                                    countWithEntities.getT2(),
                                    pageable,
                                    countWithEntities.getT1()
                                )
                            )
                        )
                        .body(countWithEntities.getT2())
                );
            });
    }

    /**
     * {@code GET  /clinicas/:id} : get the "id" clinica.
     *
     * @param id the id of the clinicaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clinicaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClinicaDTO>> getClinica(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Clinica : {}", id);
        Mono<ClinicaDTO> clinicaDTO = clinicaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clinicaDTO);
    }

    /**
     * {@code DELETE  /clinicas/:id} : delete the "id" clinica.
     *
     * @param id the id of the clinicaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteClinica(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Clinica : {}", id);
        return clinicaService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
