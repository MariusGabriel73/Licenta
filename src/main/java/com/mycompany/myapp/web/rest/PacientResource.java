package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PacientRepository;
import com.mycompany.myapp.service.PacientService;
import com.mycompany.myapp.service.dto.PacientDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Pacient}.
 */
@RestController
@RequestMapping("/api/pacients")
public class PacientResource {

    private static final Logger LOG = LoggerFactory.getLogger(PacientResource.class);

    private static final String ENTITY_NAME = "pacient";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PacientService pacientService;

    private final PacientRepository pacientRepository;

    public PacientResource(PacientService pacientService, PacientRepository pacientRepository) {
        this.pacientService = pacientService;
        this.pacientRepository = pacientRepository;
    }

    /**
     * {@code POST  /pacients} : Create a new pacient.
     *
     * @param pacientDTO the pacientDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pacientDTO, or with status {@code 400 (Bad Request)} if the pacient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<PacientDTO>> createPacient(@Valid @RequestBody PacientDTO pacientDTO) throws URISyntaxException {
        LOG.debug("REST request to save Pacient : {}", pacientDTO);
        if (pacientDTO.getId() != null) {
            throw new BadRequestAlertException("A new pacient cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pacientService
            .save(pacientDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/pacients/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /pacients/:id} : Updates an existing pacient.
     *
     * @param id the id of the pacientDTO to save.
     * @param pacientDTO the pacientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pacientDTO,
     * or with status {@code 400 (Bad Request)} if the pacientDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pacientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<PacientDTO>> updatePacient(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PacientDTO pacientDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Pacient : {}, {}", id, pacientDTO);
        if (pacientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pacientDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pacientRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return pacientService
                    .update(pacientDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /pacients/:id} : Partial updates given fields of an existing pacient, field will ignore if it is null
     *
     * @param id the id of the pacientDTO to save.
     * @param pacientDTO the pacientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pacientDTO,
     * or with status {@code 400 (Bad Request)} if the pacientDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pacientDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pacientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PacientDTO>> partialUpdatePacient(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PacientDTO pacientDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Pacient partially : {}, {}", id, pacientDTO);
        if (pacientDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pacientDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pacientRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PacientDTO> result = pacientService.partialUpdate(pacientDTO);

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
     * {@code GET  /pacients} : get all the pacients.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pacients in body.
     */
    /**
     * {@code GET  /pacients} : get all the pacients.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param userId the userId to filter by.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pacients in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PacientDTO>>> getAllPacients(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "userId.equals", required = false) Long userId
    ) {
        LOG.debug("REST request to get a page of Pacients with userId: {}", userId);
        if (userId != null) {
            return pacientService.findByUserId(userId).map(List::of).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.ok(List.of()));
        }
        return pacientService
            .countAll()
            .zipWith(pacientService.findAll(pageable).collectList())
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
     * {@code GET  /pacients/:id} : get the "id" pacient.
     *
     * @param id the id of the pacientDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pacientDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<PacientDTO>> getPacient(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Pacient : {}", id);
        Mono<PacientDTO> pacientDTO = pacientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pacientDTO);
    }

    /**
     * {@code GET  /pacients/me} : get the Pacient record for the currently authenticated user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pacientDTO, or {@code 404}.
     */
    @GetMapping("/me")
    public Mono<ResponseEntity<PacientDTO>> getCurrentPacient() {
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
            .map(ctx -> ctx.getAuthentication().getName())
            .flatMap(login -> pacientService.findByUserLogin(login))
            .map(p -> ResponseEntity.ok().body(p))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /pacients/:id} : delete the "id" pacient.
     *
     * @param id the id of the pacientDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePacient(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Pacient : {}", id);
        return pacientService
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
