package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.MedicRepository;
import com.mycompany.myapp.service.MedicService;
import com.mycompany.myapp.service.dto.MedicDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Medic}.
 */
@RestController
@RequestMapping("/api/medics")
public class MedicResource {

    private static final Logger LOG = LoggerFactory.getLogger(MedicResource.class);

    private static final String ENTITY_NAME = "medic";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MedicService medicService;

    private final MedicRepository medicRepository;

    public MedicResource(MedicService medicService, MedicRepository medicRepository) {
        this.medicService = medicService;
        this.medicRepository = medicRepository;
    }

    /**
     * {@code POST  /medics} : Create a new medic.
     *
     * @param medicDTO the medicDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new medicDTO, or with status {@code 400 (Bad Request)} if the medic has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<MedicDTO>> createMedic(@RequestBody MedicDTO medicDTO) throws URISyntaxException {
        LOG.debug("REST request to save Medic : {}", medicDTO);
        if (medicDTO.getId() != null) {
            throw new BadRequestAlertException("A new medic cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return medicService
            .save(medicDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/medics/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /medics/:id} : Updates an existing medic.
     *
     * @param id the id of the medicDTO to save.
     * @param medicDTO the medicDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated medicDTO,
     * or with status {@code 400 (Bad Request)} if the medicDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the medicDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<MedicDTO>> updateMedic(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MedicDTO medicDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Medic : {}, {}", id, medicDTO);
        if (medicDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, medicDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return medicRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return medicService
                    .update(medicDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /medics/:id} : Partial updates given fields of an existing medic, field will ignore if it is null
     *
     * @param id the id of the medicDTO to save.
     * @param medicDTO the medicDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated medicDTO,
     * or with status {@code 400 (Bad Request)} if the medicDTO is not valid,
     * or with status {@code 404 (Not Found)} if the medicDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the medicDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MedicDTO>> partialUpdateMedic(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MedicDTO medicDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Medic partially : {}, {}", id, medicDTO);
        if (medicDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, medicDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return medicRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MedicDTO> result = medicService.partialUpdate(medicDTO);

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
     * {@code GET  /medics} : get all the medics.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @param clinicaId optional filter — when set, returns only medics associated with this clinic.
     * @param sectieId optional filter — when set together with clinicaId, further filters by department.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of medics in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<MedicDTO>>> getAllMedics(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload,
        @RequestParam(name = "clinicaId", required = false) Long clinicaId,
        @RequestParam(name = "sectieId", required = false) Long sectieId
    ) {
        LOG.debug("REST request to get a page of Medics, clinicaId={}, sectieId={}", clinicaId, sectieId);
        if (clinicaId != null) {
            return medicService.findByCascadeFilter(clinicaId, sectieId).collectList().map(ResponseEntity::ok);
        }
        return medicService
            .countAll()
            .zipWith((eagerload ? medicService.findAllWithEagerRelationships(pageable) : medicService.findAll(pageable)).collectList())
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
     * {@code GET  /medics/:id} : get the "id" medic.
     *
     * @param id the id of the medicDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the medicDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<MedicDTO>> getMedic(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Medic : {}", id);
        Mono<MedicDTO> medicDTO = medicService.findOne(id);
        return ResponseUtil.wrapOrNotFound(medicDTO);
    }

    /**
     * {@code DELETE  /medics/:id} : delete the "id" medic.
     *
     * @param id the id of the medicDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteMedic(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Medic : {}", id);
        return medicService
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
