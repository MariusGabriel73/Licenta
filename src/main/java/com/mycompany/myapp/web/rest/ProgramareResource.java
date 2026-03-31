package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ProgramareRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.ProgramareService;
import com.mycompany.myapp.service.dto.ProgramareDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Programare}.
 */
@RestController
@RequestMapping("/api/programares")
public class ProgramareResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProgramareResource.class);

    private static final String ENTITY_NAME = "programare";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProgramareService programareService;

    private final ProgramareRepository programareRepository;

    public ProgramareResource(ProgramareService programareService, ProgramareRepository programareRepository) {
        this.programareService = programareService;
        this.programareRepository = programareRepository;
    }

    /**
     * {@code POST  /programares} : Create a new programare.
     *
     * @param programareDTO the programareDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new programareDTO, or with status {@code 400 (Bad Request)} if the programare has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ProgramareDTO>> createProgramare(@RequestBody ProgramareDTO programareDTO) throws URISyntaxException {
        LOG.debug("REST request to save Programare : {}", programareDTO);
        if (programareDTO.getId() != null) {
            throw new BadRequestAlertException("A new programare cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return programareService
            .save(programareDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/programares/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /programares/:id} : Updates an existing programare.
     *
     * @param id the id of the programareDTO to save.
     * @param programareDTO the programareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programareDTO,
     * or with status {@code 400 (Bad Request)} if the programareDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the programareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProgramareDTO>> updateProgramare(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProgramareDTO programareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Programare : {}, {}", id, programareDTO);
        if (programareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, programareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return programareRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return programareService
                    .update(programareDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /programares/:id} : Partial updates given fields of an existing programare, field will ignore if it is null
     *
     * @param id the id of the programareDTO to save.
     * @param programareDTO the programareDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated programareDTO,
     * or with status {@code 400 (Bad Request)} if the programareDTO is not valid,
     * or with status {@code 404 (Not Found)} if the programareDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the programareDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProgramareDTO>> partialUpdateProgramare(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProgramareDTO programareDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Programare partially : {}, {}", id, programareDTO);
        if (programareDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, programareDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return programareRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProgramareDTO> result = programareService.partialUpdate(programareDTO);

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
     * {@code GET  /programares} : get all the programares.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of programares in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProgramareDTO>>> getAllProgramares(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "filter", required = false) String filter,
        @RequestParam(name = "dataProgramare.greaterThanOrEqual", required = false) java.time.Instant dataStart,
        @RequestParam(name = "dataProgramare.lessThan", required = false) java.time.Instant dataEnd,
        @RequestParam(name = "medicId.equals", required = false) Long medicId,
        @RequestParam(name = "locatieId.equals", required = false) Long locatieId,
        @RequestParam(name = "pacientId.equals", required = false) Long pacientId
    ) {
        if ("fisamedicala-is-null".equals(filter)) {
            LOG.debug("REST request to get all Programares where fisaMedicala is null");
            return programareService.findAllWhereFisaMedicalaIsNull().collectList().map(ResponseEntity::ok);
        }

        if ("raportprogramare-is-null".equals(filter)) {
            LOG.debug("REST request to get all Programares where raportProgramare is null");
            return programareService.findAllWhereRaportProgramareIsNull().collectList().map(ResponseEntity::ok);
        }

        if (medicId != null && locatieId != null && dataStart != null && dataEnd != null) {
            LOG.debug(
                "REST request to get Programares for medic {} and locatie {} between {} and {}",
                medicId,
                locatieId,
                dataStart,
                dataEnd
            );
            return programareService
                .findByMedicIdAndLocatieIdAndDataProgramareBetween(medicId, locatieId, dataStart, dataEnd)
                .collectList()
                .map(ResponseEntity::ok);
        }
        LOG.debug("REST request to get a page of Programares");
        return SecurityUtils.hasCurrentUserThisAuthority("ROLE_MEDIC").flatMap(isMedic -> {
            if (Boolean.TRUE.equals(isMedic)) {
                java.time.Instant finalStart = dataStart != null ? dataStart : java.time.Instant.parse("2000-01-01T00:00:00Z");
                java.time.Instant finalEnd = dataEnd != null ? dataEnd : java.time.Instant.parse("2099-01-01T00:00:00Z");
                return SecurityUtils.getCurrentUserLogin()
                    .flatMap(login ->
                        programareService
                            .countAllByMedicUserLogin(login, finalStart, finalEnd)
                            .zipWith(programareService.findAllByMedicUserLogin(login, finalStart, finalEnd, pageable).collectList())
                            .map(countWithEntities ->
                                ResponseEntity.ok()
                                    .headers(
                                        PaginationUtil.generatePaginationHttpHeaders(
                                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                                        )
                                    )
                                    .body(countWithEntities.getT2())
                            )
                    );
            } else if (pacientId != null) {
                return programareService
                    .countAllByPacientId(pacientId)
                    .zipWith(programareService.findAllByPacientId(pacientId, pageable).collectList())
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
            } else {
                return programareService
                    .countAll()
                    .zipWith(programareService.findAll(pageable).collectList())
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
        });
    }

    /**
     * {@code GET  /programares/:id} : get the "id" programare.
     *
     * @param id the id of the programareDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the programareDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProgramareDTO>> getProgramare(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Programare : {}", id);
        Mono<ProgramareDTO> programareDTO = programareService.findOne(id);
        return ResponseUtil.wrapOrNotFound(programareDTO);
    }

    /**
     * {@code DELETE  /programares/:id} : delete the "id" programare.
     *
     * @param id the id of the programareDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProgramare(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Programare : {}", id);
        return programareService
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
