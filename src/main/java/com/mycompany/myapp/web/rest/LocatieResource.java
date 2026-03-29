package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.LocatieRepository;
import com.mycompany.myapp.service.LocatieService;
import com.mycompany.myapp.service.dto.LocatieDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Locatie}.
 */
@RestController
@RequestMapping("/api/locaties")
public class LocatieResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocatieResource.class);

    private static final String ENTITY_NAME = "locatie";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LocatieService locatieService;

    private final LocatieRepository locatieRepository;

    public LocatieResource(LocatieService locatieService, LocatieRepository locatieRepository) {
        this.locatieService = locatieService;
        this.locatieRepository = locatieRepository;
    }

    /**
     * {@code POST  /locaties} : Create a new locatie.
     *
     * @param locatieDTO the locatieDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locatieDTO, or with status {@code 400 (Bad Request)} if the locatie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<LocatieDTO>> createLocatie(@RequestBody LocatieDTO locatieDTO) throws URISyntaxException {
        LOG.debug("REST request to save Locatie : {}", locatieDTO);
        if (locatieDTO.getId() != null) {
            throw new BadRequestAlertException("A new locatie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return locatieService
            .save(locatieDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/locaties/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /locaties/:id} : Updates an existing locatie.
     *
     * @param id the id of the locatieDTO to save.
     * @param locatieDTO the locatieDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locatieDTO,
     * or with status {@code 400 (Bad Request)} if the locatieDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locatieDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<LocatieDTO>> updateLocatie(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LocatieDTO locatieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Locatie : {}, {}", id, locatieDTO);
        if (locatieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locatieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return locatieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return locatieService
                    .update(locatieDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /locaties/:id} : Partial updates given fields of an existing locatie, field will ignore if it is null
     *
     * @param id the id of the locatieDTO to save.
     * @param locatieDTO the locatieDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locatieDTO,
     * or with status {@code 400 (Bad Request)} if the locatieDTO is not valid,
     * or with status {@code 404 (Not Found)} if the locatieDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the locatieDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<LocatieDTO>> partialUpdateLocatie(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LocatieDTO locatieDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Locatie partially : {}, {}", id, locatieDTO);
        if (locatieDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locatieDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return locatieRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<LocatieDTO> result = locatieService.partialUpdate(locatieDTO);

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
     * {@code GET  /locaties} : get all the locaties.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of locaties in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<LocatieDTO>>> getAllLocaties(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Locaties");
        return locatieService
            .countAll()
            .zipWith(locatieService.findAll(pageable).collectList())
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
     * {@code GET  /locaties/:id} : get the "id" locatie.
     *
     * @param id the id of the locatieDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locatieDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<LocatieDTO>> getLocatie(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Locatie : {}", id);
        Mono<LocatieDTO> locatieDTO = locatieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(locatieDTO);
    }

    /**
     * {@code DELETE  /locaties/:id} : delete the "id" locatie.
     *
     * @param id the id of the locatieDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLocatie(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Locatie : {}", id);
        return locatieService
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
