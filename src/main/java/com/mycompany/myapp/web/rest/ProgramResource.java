package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ProgramRepository;
import com.mycompany.myapp.service.ProgramService;
import com.mycompany.myapp.service.dto.ProgramDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Program}.
 */
@RestController
@RequestMapping("/api/programs")
public class ProgramResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramResource.class);

    private static final String ENTITY_NAME = "program";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProgramService programService;

    private final ProgramRepository programRepository;

    public ProgramResource(ProgramService programService, ProgramRepository programRepository) {
        this.programService = programService;
        this.programRepository = programRepository;
    }

    /**
     * {@code POST  /programs} : Create a new program.
     *
     * @param programDTO the programDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programDTO, or with status {@code 400 (Bad Request)} if the program has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ProgramDTO>> createProgram(@RequestBody ProgramDTO programDTO) throws URISyntaxException {
        LOG.debug("REST request to save Program : {}", programDTO);
        if (programDTO.getId() != null) {
            throw new BadRequestAlertException("A new program cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return programService
            .save(programDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/programs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /programs/:id} : Updates an existing program.
     *
     * @param id the id of the programDTO to save.
     * @param programDTO the programDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programDTO,
     * or with status {@code 400 (Bad Request)} if the programDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the programDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProgramDTO>> updateProgram(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProgramDTO programDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Program : {}, {}", id, programDTO);
        if (programDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, programDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return programRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return programService
                    .update(programDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /programs/:id} : Partial updates given fields of an existing program, field will ignore if it is null
     *
     * @param id the id of the programDTO to save.
     * @param programDTO the programDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programDTO,
     * or with status {@code 400 (Bad Request)} if the programDTO is not valid,
     * or with status {@code 404 (Not Found)} if the programDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the programDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProgramDTO>> partialUpdateProgram(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProgramDTO programDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Program partially : {}, {}", id, programDTO);
        if (programDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, programDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return programRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProgramDTO> result = programService.partialUpdate(programDTO);

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
     * {@code GET  /programs} : get all the programs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProgramDTO>>> getAllPrograms(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "medicId.equals", required = false) Long medicId,
        @RequestParam(name = "locatieId.equals", required = false) Long locatieId
    ) {
        LOG.debug("REST request to get a page of Programs");
        if (medicId != null && locatieId != null) {
            LOG.debug("Filtering programs by medicId: {} and locatieId: {}", medicId, locatieId);
            return programService
                .findByMedicAndLocatie(medicId, locatieId)
                .collectList()
                .doOnNext(list -> {
                    if (list.isEmpty()) {
                        LOG.warn("NO PROGRAMS FOUND in database for medicId: {} and locatieId: {}", medicId, locatieId);
                    } else {
                        LOG.debug("Found {} programs for medicId: {} and locatieId: {}", list.size(), medicId, locatieId);
                    }
                })
                .map(list -> ResponseEntity.ok().body(list));
        }
        LOG.warn(
            "REST request to get programs WITHOUT both medicId and locatieId filters. Returning all programs (potential performance issue)."
        );
        return programService
            .countAll()
            .zipWith(programService.findAll(pageable).collectList())
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
     * {@code GET  /programs/:id} : get the "id" program.
     *
     * @param id the id of the programDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProgramDTO>> getProgram(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Program : {}", id);
        Mono<ProgramDTO> programDTO = programService.findOne(id);
        return ResponseUtil.wrapOrNotFound(programDTO);
    }

    /**
     * {@code DELETE  /programs/:id} : delete the "id" program.
     *
     * @param id the id of the programDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProgram(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Program : {}", id);
        return programService
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
