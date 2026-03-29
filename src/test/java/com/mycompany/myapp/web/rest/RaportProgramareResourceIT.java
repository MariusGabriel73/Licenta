package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.RaportProgramareAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.RaportProgramare;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.RaportProgramareRepository;
import com.mycompany.myapp.service.dto.RaportProgramareDTO;
import com.mycompany.myapp.service.mapper.RaportProgramareMapper;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RaportProgramareResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RaportProgramareResourceIT {

    private static final ZonedDateTime DEFAULT_ORA_PROGRAMATA = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ORA_PROGRAMATA = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ORA_INCEPUT_CONSULTATIE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ORA_INCEPUT_CONSULTATIE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_DURATA_CONSULTATIE = 1;
    private static final Integer UPDATED_DURATA_CONSULTATIE = 2;

    private static final String ENTITY_API_URL = "/api/raport-programares";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RaportProgramareRepository raportProgramareRepository;

    @Autowired
    private RaportProgramareMapper raportProgramareMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RaportProgramare raportProgramare;

    private RaportProgramare insertedRaportProgramare;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RaportProgramare createEntity() {
        return new RaportProgramare()
            .oraProgramata(DEFAULT_ORA_PROGRAMATA)
            .oraInceputConsultatie(DEFAULT_ORA_INCEPUT_CONSULTATIE)
            .durataConsultatie(DEFAULT_DURATA_CONSULTATIE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RaportProgramare createUpdatedEntity() {
        return new RaportProgramare()
            .oraProgramata(UPDATED_ORA_PROGRAMATA)
            .oraInceputConsultatie(UPDATED_ORA_INCEPUT_CONSULTATIE)
            .durataConsultatie(UPDATED_DURATA_CONSULTATIE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RaportProgramare.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        raportProgramare = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRaportProgramare != null) {
            raportProgramareRepository.delete(insertedRaportProgramare).block();
            insertedRaportProgramare = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRaportProgramare() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);
        var returnedRaportProgramareDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RaportProgramareDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the RaportProgramare in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRaportProgramare = raportProgramareMapper.toEntity(returnedRaportProgramareDTO);
        assertRaportProgramareUpdatableFieldsEquals(returnedRaportProgramare, getPersistedRaportProgramare(returnedRaportProgramare));

        insertedRaportProgramare = returnedRaportProgramare;
    }

    @Test
    void createRaportProgramareWithExistingId() throws Exception {
        // Create the RaportProgramare with an existing ID
        raportProgramare.setId(1L);
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRaportProgramares() {
        // Initialize the database
        insertedRaportProgramare = raportProgramareRepository.save(raportProgramare).block();

        // Get all the raportProgramareList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(raportProgramare.getId().intValue()))
            .jsonPath("$.[*].oraProgramata")
            .value(hasItem(sameInstant(DEFAULT_ORA_PROGRAMATA)))
            .jsonPath("$.[*].oraInceputConsultatie")
            .value(hasItem(sameInstant(DEFAULT_ORA_INCEPUT_CONSULTATIE)))
            .jsonPath("$.[*].durataConsultatie")
            .value(hasItem(DEFAULT_DURATA_CONSULTATIE));
    }

    @Test
    void getRaportProgramare() {
        // Initialize the database
        insertedRaportProgramare = raportProgramareRepository.save(raportProgramare).block();

        // Get the raportProgramare
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, raportProgramare.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(raportProgramare.getId().intValue()))
            .jsonPath("$.oraProgramata")
            .value(is(sameInstant(DEFAULT_ORA_PROGRAMATA)))
            .jsonPath("$.oraInceputConsultatie")
            .value(is(sameInstant(DEFAULT_ORA_INCEPUT_CONSULTATIE)))
            .jsonPath("$.durataConsultatie")
            .value(is(DEFAULT_DURATA_CONSULTATIE));
    }

    @Test
    void getNonExistingRaportProgramare() {
        // Get the raportProgramare
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRaportProgramare() throws Exception {
        // Initialize the database
        insertedRaportProgramare = raportProgramareRepository.save(raportProgramare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the raportProgramare
        RaportProgramare updatedRaportProgramare = raportProgramareRepository.findById(raportProgramare.getId()).block();
        updatedRaportProgramare
            .oraProgramata(UPDATED_ORA_PROGRAMATA)
            .oraInceputConsultatie(UPDATED_ORA_INCEPUT_CONSULTATIE)
            .durataConsultatie(UPDATED_DURATA_CONSULTATIE);
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(updatedRaportProgramare);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, raportProgramareDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRaportProgramareToMatchAllProperties(updatedRaportProgramare);
    }

    @Test
    void putNonExistingRaportProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        raportProgramare.setId(longCount.incrementAndGet());

        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, raportProgramareDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRaportProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        raportProgramare.setId(longCount.incrementAndGet());

        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRaportProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        raportProgramare.setId(longCount.incrementAndGet());

        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRaportProgramareWithPatch() throws Exception {
        // Initialize the database
        insertedRaportProgramare = raportProgramareRepository.save(raportProgramare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the raportProgramare using partial update
        RaportProgramare partialUpdatedRaportProgramare = new RaportProgramare();
        partialUpdatedRaportProgramare.setId(raportProgramare.getId());

        partialUpdatedRaportProgramare.oraProgramata(UPDATED_ORA_PROGRAMATA).oraInceputConsultatie(UPDATED_ORA_INCEPUT_CONSULTATIE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRaportProgramare.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRaportProgramare))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RaportProgramare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRaportProgramareUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRaportProgramare, raportProgramare),
            getPersistedRaportProgramare(raportProgramare)
        );
    }

    @Test
    void fullUpdateRaportProgramareWithPatch() throws Exception {
        // Initialize the database
        insertedRaportProgramare = raportProgramareRepository.save(raportProgramare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the raportProgramare using partial update
        RaportProgramare partialUpdatedRaportProgramare = new RaportProgramare();
        partialUpdatedRaportProgramare.setId(raportProgramare.getId());

        partialUpdatedRaportProgramare
            .oraProgramata(UPDATED_ORA_PROGRAMATA)
            .oraInceputConsultatie(UPDATED_ORA_INCEPUT_CONSULTATIE)
            .durataConsultatie(UPDATED_DURATA_CONSULTATIE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRaportProgramare.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRaportProgramare))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RaportProgramare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRaportProgramareUpdatableFieldsEquals(
            partialUpdatedRaportProgramare,
            getPersistedRaportProgramare(partialUpdatedRaportProgramare)
        );
    }

    @Test
    void patchNonExistingRaportProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        raportProgramare.setId(longCount.incrementAndGet());

        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, raportProgramareDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRaportProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        raportProgramare.setId(longCount.incrementAndGet());

        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRaportProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        raportProgramare.setId(longCount.incrementAndGet());

        // Create the RaportProgramare
        RaportProgramareDTO raportProgramareDTO = raportProgramareMapper.toDto(raportProgramare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(raportProgramareDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RaportProgramare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRaportProgramare() {
        // Initialize the database
        insertedRaportProgramare = raportProgramareRepository.save(raportProgramare).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the raportProgramare
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, raportProgramare.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return raportProgramareRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected RaportProgramare getPersistedRaportProgramare(RaportProgramare raportProgramare) {
        return raportProgramareRepository.findById(raportProgramare.getId()).block();
    }

    protected void assertPersistedRaportProgramareToMatchAllProperties(RaportProgramare expectedRaportProgramare) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRaportProgramareAllPropertiesEquals(expectedRaportProgramare, getPersistedRaportProgramare(expectedRaportProgramare));
        assertRaportProgramareUpdatableFieldsEquals(expectedRaportProgramare, getPersistedRaportProgramare(expectedRaportProgramare));
    }

    protected void assertPersistedRaportProgramareToMatchUpdatableProperties(RaportProgramare expectedRaportProgramare) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRaportProgramareAllUpdatablePropertiesEquals(expectedRaportProgramare, getPersistedRaportProgramare(expectedRaportProgramare));
        assertRaportProgramareUpdatableFieldsEquals(expectedRaportProgramare, getPersistedRaportProgramare(expectedRaportProgramare));
    }
}
