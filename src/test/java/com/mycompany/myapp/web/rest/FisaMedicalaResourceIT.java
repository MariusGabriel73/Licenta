package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FisaMedicalaAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.FisaMedicala;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.FisaMedicalaRepository;
import com.mycompany.myapp.service.dto.FisaMedicalaDTO;
import com.mycompany.myapp.service.mapper.FisaMedicalaMapper;
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
 * Integration tests for the {@link FisaMedicalaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FisaMedicalaResourceIT {

    private static final String DEFAULT_DIAGNOSTIC = "AAAAAAAAAA";
    private static final String UPDATED_DIAGNOSTIC = "BBBBBBBBBB";

    private static final String DEFAULT_TRATAMENT = "AAAAAAAAAA";
    private static final String UPDATED_TRATAMENT = "BBBBBBBBBB";

    private static final String DEFAULT_RECOMANDARI = "AAAAAAAAAA";
    private static final String UPDATED_RECOMANDARI = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATA_CONSULTATIE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_CONSULTATIE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/fisa-medicalas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FisaMedicalaRepository fisaMedicalaRepository;

    @Autowired
    private FisaMedicalaMapper fisaMedicalaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private FisaMedicala fisaMedicala;

    private FisaMedicala insertedFisaMedicala;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FisaMedicala createEntity() {
        return new FisaMedicala()
            .diagnostic(DEFAULT_DIAGNOSTIC)
            .tratament(DEFAULT_TRATAMENT)
            .recomandari(DEFAULT_RECOMANDARI)
            .dataConsultatie(DEFAULT_DATA_CONSULTATIE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FisaMedicala createUpdatedEntity() {
        return new FisaMedicala()
            .diagnostic(UPDATED_DIAGNOSTIC)
            .tratament(UPDATED_TRATAMENT)
            .recomandari(UPDATED_RECOMANDARI)
            .dataConsultatie(UPDATED_DATA_CONSULTATIE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(FisaMedicala.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        fisaMedicala = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFisaMedicala != null) {
            fisaMedicalaRepository.delete(insertedFisaMedicala).block();
            insertedFisaMedicala = null;
        }
        deleteEntities(em);
    }

    @Test
    void createFisaMedicala() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);
        var returnedFisaMedicalaDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(FisaMedicalaDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the FisaMedicala in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFisaMedicala = fisaMedicalaMapper.toEntity(returnedFisaMedicalaDTO);
        assertFisaMedicalaUpdatableFieldsEquals(returnedFisaMedicala, getPersistedFisaMedicala(returnedFisaMedicala));

        insertedFisaMedicala = returnedFisaMedicala;
    }

    @Test
    void createFisaMedicalaWithExistingId() throws Exception {
        // Create the FisaMedicala with an existing ID
        fisaMedicala.setId(1L);
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFisaMedicalas() {
        // Initialize the database
        insertedFisaMedicala = fisaMedicalaRepository.save(fisaMedicala).block();

        // Get all the fisaMedicalaList
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
            .value(hasItem(fisaMedicala.getId().intValue()))
            .jsonPath("$.[*].diagnostic")
            .value(hasItem(DEFAULT_DIAGNOSTIC))
            .jsonPath("$.[*].tratament")
            .value(hasItem(DEFAULT_TRATAMENT))
            .jsonPath("$.[*].recomandari")
            .value(hasItem(DEFAULT_RECOMANDARI))
            .jsonPath("$.[*].dataConsultatie")
            .value(hasItem(sameInstant(DEFAULT_DATA_CONSULTATIE)));
    }

    @Test
    void getFisaMedicala() {
        // Initialize the database
        insertedFisaMedicala = fisaMedicalaRepository.save(fisaMedicala).block();

        // Get the fisaMedicala
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, fisaMedicala.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(fisaMedicala.getId().intValue()))
            .jsonPath("$.diagnostic")
            .value(is(DEFAULT_DIAGNOSTIC))
            .jsonPath("$.tratament")
            .value(is(DEFAULT_TRATAMENT))
            .jsonPath("$.recomandari")
            .value(is(DEFAULT_RECOMANDARI))
            .jsonPath("$.dataConsultatie")
            .value(is(sameInstant(DEFAULT_DATA_CONSULTATIE)));
    }

    @Test
    void getNonExistingFisaMedicala() {
        // Get the fisaMedicala
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingFisaMedicala() throws Exception {
        // Initialize the database
        insertedFisaMedicala = fisaMedicalaRepository.save(fisaMedicala).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fisaMedicala
        FisaMedicala updatedFisaMedicala = fisaMedicalaRepository.findById(fisaMedicala.getId()).block();
        updatedFisaMedicala
            .diagnostic(UPDATED_DIAGNOSTIC)
            .tratament(UPDATED_TRATAMENT)
            .recomandari(UPDATED_RECOMANDARI)
            .dataConsultatie(UPDATED_DATA_CONSULTATIE);
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(updatedFisaMedicala);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fisaMedicalaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFisaMedicalaToMatchAllProperties(updatedFisaMedicala);
    }

    @Test
    void putNonExistingFisaMedicala() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fisaMedicala.setId(longCount.incrementAndGet());

        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, fisaMedicalaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFisaMedicala() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fisaMedicala.setId(longCount.incrementAndGet());

        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFisaMedicala() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fisaMedicala.setId(longCount.incrementAndGet());

        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFisaMedicalaWithPatch() throws Exception {
        // Initialize the database
        insertedFisaMedicala = fisaMedicalaRepository.save(fisaMedicala).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fisaMedicala using partial update
        FisaMedicala partialUpdatedFisaMedicala = new FisaMedicala();
        partialUpdatedFisaMedicala.setId(fisaMedicala.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFisaMedicala.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFisaMedicala))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FisaMedicala in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFisaMedicalaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFisaMedicala, fisaMedicala),
            getPersistedFisaMedicala(fisaMedicala)
        );
    }

    @Test
    void fullUpdateFisaMedicalaWithPatch() throws Exception {
        // Initialize the database
        insertedFisaMedicala = fisaMedicalaRepository.save(fisaMedicala).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fisaMedicala using partial update
        FisaMedicala partialUpdatedFisaMedicala = new FisaMedicala();
        partialUpdatedFisaMedicala.setId(fisaMedicala.getId());

        partialUpdatedFisaMedicala
            .diagnostic(UPDATED_DIAGNOSTIC)
            .tratament(UPDATED_TRATAMENT)
            .recomandari(UPDATED_RECOMANDARI)
            .dataConsultatie(UPDATED_DATA_CONSULTATIE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFisaMedicala.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedFisaMedicala))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the FisaMedicala in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFisaMedicalaUpdatableFieldsEquals(partialUpdatedFisaMedicala, getPersistedFisaMedicala(partialUpdatedFisaMedicala));
    }

    @Test
    void patchNonExistingFisaMedicala() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fisaMedicala.setId(longCount.incrementAndGet());

        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, fisaMedicalaDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFisaMedicala() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fisaMedicala.setId(longCount.incrementAndGet());

        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFisaMedicala() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fisaMedicala.setId(longCount.incrementAndGet());

        // Create the FisaMedicala
        FisaMedicalaDTO fisaMedicalaDTO = fisaMedicalaMapper.toDto(fisaMedicala);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(fisaMedicalaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the FisaMedicala in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFisaMedicala() {
        // Initialize the database
        insertedFisaMedicala = fisaMedicalaRepository.save(fisaMedicala).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fisaMedicala
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, fisaMedicala.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return fisaMedicalaRepository.count().block();
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

    protected FisaMedicala getPersistedFisaMedicala(FisaMedicala fisaMedicala) {
        return fisaMedicalaRepository.findById(fisaMedicala.getId()).block();
    }

    protected void assertPersistedFisaMedicalaToMatchAllProperties(FisaMedicala expectedFisaMedicala) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFisaMedicalaAllPropertiesEquals(expectedFisaMedicala, getPersistedFisaMedicala(expectedFisaMedicala));
        assertFisaMedicalaUpdatableFieldsEquals(expectedFisaMedicala, getPersistedFisaMedicala(expectedFisaMedicala));
    }

    protected void assertPersistedFisaMedicalaToMatchUpdatableProperties(FisaMedicala expectedFisaMedicala) {
        // Test fails because reactive api returns an empty object instead of null
        // assertFisaMedicalaAllUpdatablePropertiesEquals(expectedFisaMedicala, getPersistedFisaMedicala(expectedFisaMedicala));
        assertFisaMedicalaUpdatableFieldsEquals(expectedFisaMedicala, getPersistedFisaMedicala(expectedFisaMedicala));
    }
}
