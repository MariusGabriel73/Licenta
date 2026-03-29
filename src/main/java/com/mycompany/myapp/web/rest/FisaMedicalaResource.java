package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FisaMedicalaRepository;
import com.mycompany.myapp.service.FisaMedicalaService;
import com.mycompany.myapp.service.dto.FisaMedicalaDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.FisaMedicala}.
 */
@RestController
@RequestMapping("/api/fisa-medicalas")
public class FisaMedicalaResource {

    private static final Logger LOG = LoggerFactory.getLogger(FisaMedicalaResource.class);

    private static final String ENTITY_NAME = "fisaMedicala";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FisaMedicalaService fisaMedicalaService;

    private final FisaMedicalaRepository fisaMedicalaRepository;

    public FisaMedicalaResource(FisaMedicalaService fisaMedicalaService, FisaMedicalaRepository fisaMedicalaRepository) {
        this.fisaMedicalaService = fisaMedicalaService;
        this.fisaMedicalaRepository = fisaMedicalaRepository;
    }

    /**
     * {@code POST  /fisa-medicalas} : Create a new fisaMedicala.
     *
     * @param fisaMedicalaDTO the fisaMedicalaDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fisaMedicalaDTO, or with status {@code 400 (Bad Request)} if the fisaMedicala has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<FisaMedicalaDTO>> createFisaMedicala(@RequestBody FisaMedicalaDTO fisaMedicalaDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FisaMedicala : {}", fisaMedicalaDTO);
        if (fisaMedicalaDTO.getId() != null) {
            throw new BadRequestAlertException("A new fisaMedicala cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return fisaMedicalaService
            .save(fisaMedicalaDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/fisa-medicalas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /fisa-medicalas/:id} : Updates an existing fisaMedicala.
     *
     * @param id the id of the fisaMedicalaDTO to save.
     * @param fisaMedicalaDTO the fisaMedicalaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fisaMedicalaDTO,
     * or with status {@code 400 (Bad Request)} if the fisaMedicalaDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fisaMedicalaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<FisaMedicalaDTO>> updateFisaMedicala(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FisaMedicalaDTO fisaMedicalaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FisaMedicala : {}, {}", id, fisaMedicalaDTO);
        if (fisaMedicalaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fisaMedicalaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fisaMedicalaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return fisaMedicalaService
                    .update(fisaMedicalaDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /fisa-medicalas/:id} : Partial updates given fields of an existing fisaMedicala, field will ignore if it is null
     *
     * @param id the id of the fisaMedicalaDTO to save.
     * @param fisaMedicalaDTO the fisaMedicalaDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fisaMedicalaDTO,
     * or with status {@code 400 (Bad Request)} if the fisaMedicalaDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fisaMedicalaDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fisaMedicalaDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<FisaMedicalaDTO>> partialUpdateFisaMedicala(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FisaMedicalaDTO fisaMedicalaDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FisaMedicala partially : {}, {}", id, fisaMedicalaDTO);
        if (fisaMedicalaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fisaMedicalaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return fisaMedicalaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<FisaMedicalaDTO> result = fisaMedicalaService.partialUpdate(fisaMedicalaDTO);

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
     * {@code GET  /fisa-medicalas} : get all the fisaMedicalas.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of fisaMedicalas in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<FisaMedicalaDTO>>> getAllFisaMedicalas(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of FisaMedicalas");
        return fisaMedicalaService
            .countAll()
            .zipWith(fisaMedicalaService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /fisa-medicalas/:id} : get the "id" fisaMedicala.
     *
     * @param id the id of the fisaMedicalaDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fisaMedicalaDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<FisaMedicalaDTO>> getFisaMedicala(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FisaMedicala : {}", id);
        Mono<FisaMedicalaDTO> fisaMedicalaDTO = fisaMedicalaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fisaMedicalaDTO);
    }

    /**
     * {@code DELETE  /fisa-medicalas/:id} : delete the "id" fisaMedicala.
     *
     * @param id the id of the fisaMedicalaDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFisaMedicala(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FisaMedicala : {}", id);
        return fisaMedicalaService
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
