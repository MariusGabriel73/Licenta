package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SpecializareRepository;
import com.mycompany.myapp.service.SpecializareService;
import com.mycompany.myapp.service.dto.SpecializareDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Specializare}.
 */
@RestController
@RequestMapping("/api/specializares")
public class SpecializareResource {

    private static final Logger LOG = LoggerFactory.getLogger(SpecializareResource.class);

    private static final String ENTITY_NAME = "specializare";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpecializareService specializareService;

    private final SpecializareRepository specializareRepository;

    public SpecializareResource(SpecializareService specializareService, SpecializareRepository specializareRepository) {
        this.specializareService = specializareService;
        this.specializareRepository = specializareRepository;
    }

    /**
     * {@code POST  /specializares} : Create a new specializare.
     *
     * @param specializareDTO the specializareDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new specializareDTO, or with status {@code 400 (Bad Request)} if the specializare has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SpecializareDTO>> createSpecializare(@Valid @RequestBody SpecializareDTO specializareDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Specializare : {}", specializareDTO);
        if (specializareDTO.getId() != null) {
            throw new BadRequestAlertException("A new specializare cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return specializareService
            .save(specializareDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/specializares/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /specializares/:id} : Updates an existing specializare.
     *
     * @param id the id of the specializareDTO to save.
     * @param specializareDTO the specializareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated specializareDTO,
     * or with status {@code 400 (Bad Request)} if the specializareDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the specializareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SpecializareDTO>> updateSpecializare(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SpecializareDTO specializareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Specializare : {}, {}", id, specializareDTO);
        if (specializareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, specializareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return specializareRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return specializareService
                    .update(specializareDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /specializares/:id} : Partial updates given fields of an existing specializare, field will ignore if it is null
     *
     * @param id the id of the specializareDTO to save.
     * @param specializareDTO the specializareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated specializareDTO,
     * or with status {@code 400 (Bad Request)} if the specializareDTO is not valid,
     * or with status {@code 404 (Not Found)} if the specializareDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the specializareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SpecializareDTO>> partialUpdateSpecializare(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SpecializareDTO specializareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Specializare partially : {}, {}", id, specializareDTO);
        if (specializareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, specializareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return specializareRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SpecializareDTO> result = specializareService.partialUpdate(specializareDTO);

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
     * {@code GET  /specializares} : get all the specializares.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of specializares in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SpecializareDTO>>> getAllSpecializares(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Specializares");
        return specializareService
            .countAll()
            .zipWith(specializareService.findAll(pageable).collectList())
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
     * {@code GET  /specializares/:id} : get the "id" specializare.
     *
     * @param id the id of the specializareDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the specializareDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SpecializareDTO>> getSpecializare(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Specializare : {}", id);
        Mono<SpecializareDTO> specializareDTO = specializareService.findOne(id);
        return ResponseUtil.wrapOrNotFound(specializareDTO);
    }

    /**
     * {@code DELETE  /specializares/:id} : delete the "id" specializare.
     *
     * @param id the id of the specializareDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSpecializare(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Specializare : {}", id);
        return specializareService
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
