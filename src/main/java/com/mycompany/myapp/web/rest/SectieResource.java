package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.SectieRepository;
import com.mycompany.myapp.service.SectieService;
import com.mycompany.myapp.service.dto.SectieDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Sectie}.
 */
@RestController
@RequestMapping("/api/secties")
public class SectieResource {

    private static final Logger LOG = LoggerFactory.getLogger(SectieResource.class);

    private static final String ENTITY_NAME = "sectie";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SectieService sectieService;

    private final SectieRepository sectieRepository;

    public SectieResource(SectieService sectieService, SectieRepository sectieRepository) {
        this.sectieService = sectieService;
        this.sectieRepository = sectieRepository;
    }

    /**
     * {@code POST  /secties} : Create a new sectie.
     *
     * @param sectieDTO the sectieDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sectieDTO, or with status {@code 400 (Bad Request)} if the sectie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<SectieDTO>> createSectie(@Valid @RequestBody SectieDTO sectieDTO) throws URISyntaxException {
        LOG.debug("REST request to save Sectie : {}", sectieDTO);
        if (sectieDTO.getId() != null) {
            throw new BadRequestAlertException("A new sectie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return sectieService
            .save(sectieDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/secties/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /secties/:id} : Updates an existing sectie.
     *
     * @param id the id of the sectieDTO to save.
     * @param sectieDTO the sectieDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sectieDTO,
     * or with status {@code 400 (Bad Request)} if the sectieDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sectieDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<SectieDTO>> updateSectie(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SectieDTO sectieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Sectie : {}, {}", id, sectieDTO);
        if (sectieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sectieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sectieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return sectieService
                    .update(sectieDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /secties/:id} : Partial updates given fields of an existing sectie, field will ignore if it is null
     *
     * @param id the id of the sectieDTO to save.
     * @param sectieDTO the sectieDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sectieDTO,
     * or with status {@code 400 (Bad Request)} if the sectieDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sectieDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sectieDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SectieDTO>> partialUpdateSectie(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SectieDTO sectieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Sectie partially : {}, {}", id, sectieDTO);
        if (sectieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sectieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return sectieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SectieDTO> result = sectieService.partialUpdate(sectieDTO);

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
     * {@code GET  /secties} : get all the secties.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of secties in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<SectieDTO>>> getAllSecties(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Secties");
        return sectieService
            .countAll()
            .zipWith(sectieService.findAll(pageable).collectList())
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
     * {@code GET  /secties/:id} : get the "id" sectie.
     *
     * @param id the id of the sectieDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sectieDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<SectieDTO>> getSectie(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Sectie : {}", id);
        Mono<SectieDTO> sectieDTO = sectieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sectieDTO);
    }

    /**
     * {@code DELETE  /secties/:id} : delete the "id" sectie.
     *
     * @param id the id of the sectieDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSectie(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Sectie : {}", id);
        return sectieService
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
