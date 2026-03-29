package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.RaportProgramareRepository;
import com.mycompany.myapp.service.RaportProgramareService;
import com.mycompany.myapp.service.dto.RaportProgramareDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.RaportProgramare}.
 */
@RestController
@RequestMapping("/api/raport-programares")
public class RaportProgramareResource {

    private static final Logger LOG = LoggerFactory.getLogger(RaportProgramareResource.class);

    private static final String ENTITY_NAME = "raportProgramare";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RaportProgramareService raportProgramareService;

    private final RaportProgramareRepository raportProgramareRepository;

    public RaportProgramareResource(
        RaportProgramareService raportProgramareService,
        RaportProgramareRepository raportProgramareRepository
    ) {
        this.raportProgramareService = raportProgramareService;
        this.raportProgramareRepository = raportProgramareRepository;
    }

    /**
     * {@code POST  /raport-programares} : Create a new raportProgramare.
     *
     * @param raportProgramareDTO the raportProgramareDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new raportProgramareDTO, or with status {@code 400 (Bad Request)} if the raportProgramare has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RaportProgramareDTO>> createRaportProgramare(@RequestBody RaportProgramareDTO raportProgramareDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RaportProgramare : {}", raportProgramareDTO);
        if (raportProgramareDTO.getId() != null) {
            throw new BadRequestAlertException("A new raportProgramare cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return raportProgramareService
            .save(raportProgramareDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/raport-programares/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /raport-programares/:id} : Updates an existing raportProgramare.
     *
     * @param id the id of the raportProgramareDTO to save.
     * @param raportProgramareDTO the raportProgramareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated raportProgramareDTO,
     * or with status {@code 400 (Bad Request)} if the raportProgramareDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the raportProgramareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RaportProgramareDTO>> updateRaportProgramare(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RaportProgramareDTO raportProgramareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RaportProgramare : {}, {}", id, raportProgramareDTO);
        if (raportProgramareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, raportProgramareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return raportProgramareRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return raportProgramareService
                    .update(raportProgramareDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /raport-programares/:id} : Partial updates given fields of an existing raportProgramare, field will ignore if it is null
     *
     * @param id the id of the raportProgramareDTO to save.
     * @param raportProgramareDTO the raportProgramareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated raportProgramareDTO,
     * or with status {@code 400 (Bad Request)} if the raportProgramareDTO is not valid,
     * or with status {@code 404 (Not Found)} if the raportProgramareDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the raportProgramareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RaportProgramareDTO>> partialUpdateRaportProgramare(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RaportProgramareDTO raportProgramareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RaportProgramare partially : {}, {}", id, raportProgramareDTO);
        if (raportProgramareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, raportProgramareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return raportProgramareRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RaportProgramareDTO> result = raportProgramareService.partialUpdate(raportProgramareDTO);

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
     * {@code GET  /raport-programares} : get all the raportProgramares.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of raportProgramares in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RaportProgramareDTO>>> getAllRaportProgramares(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of RaportProgramares");
        return raportProgramareService
            .countAll()
            .zipWith(raportProgramareService.findAll(pageable).collectList())
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
     * {@code GET  /raport-programares/:id} : get the "id" raportProgramare.
     *
     * @param id the id of the raportProgramareDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the raportProgramareDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RaportProgramareDTO>> getRaportProgramare(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RaportProgramare : {}", id);
        Mono<RaportProgramareDTO> raportProgramareDTO = raportProgramareService.findOne(id);
        return ResponseUtil.wrapOrNotFound(raportProgramareDTO);
    }

    /**
     * {@code DELETE  /raport-programares/:id} : delete the "id" raportProgramare.
     *
     * @param id the id of the raportProgramareDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRaportProgramare(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RaportProgramare : {}", id);
        return raportProgramareService
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
