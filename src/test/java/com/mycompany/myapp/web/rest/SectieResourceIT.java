package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SectieAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Sectie;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.SectieRepository;
import com.mycompany.myapp.service.dto.SectieDTO;
import com.mycompany.myapp.service.mapper.SectieMapper;
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
 * Integration tests for the {@link SectieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SectieResourceIT {

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/secties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SectieRepository sectieRepository;

    @Autowired
    private SectieMapper sectieMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Sectie sectie;

    private Sectie insertedSectie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sectie createEntity() {
        return new Sectie().nume(DEFAULT_NUME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sectie createUpdatedEntity() {
        return new Sectie().nume(UPDATED_NUME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Sectie.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        sectie = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSectie != null) {
            sectieRepository.delete(insertedSectie).block();
            insertedSectie = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSectie() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);
        var returnedSectieDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SectieDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Sectie in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSectie = sectieMapper.toEntity(returnedSectieDTO);
        assertSectieUpdatableFieldsEquals(returnedSectie, getPersistedSectie(returnedSectie));

        insertedSectie = returnedSectie;
    }

    @Test
    void createSectieWithExistingId() throws Exception {
        // Create the Sectie with an existing ID
        sectie.setId(1L);
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNumeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sectie.setNume(null);

        // Create the Sectie, which fails.
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSecties() {
        // Initialize the database
        insertedSectie = sectieRepository.save(sectie).block();

        // Get all the sectieList
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
            .value(hasItem(sectie.getId().intValue()))
            .jsonPath("$.[*].nume")
            .value(hasItem(DEFAULT_NUME));
    }

    @Test
    void getSectie() {
        // Initialize the database
        insertedSectie = sectieRepository.save(sectie).block();

        // Get the sectie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, sectie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(sectie.getId().intValue()))
            .jsonPath("$.nume")
            .value(is(DEFAULT_NUME));
    }

    @Test
    void getNonExistingSectie() {
        // Get the sectie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSectie() throws Exception {
        // Initialize the database
        insertedSectie = sectieRepository.save(sectie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sectie
        Sectie updatedSectie = sectieRepository.findById(sectie.getId()).block();
        updatedSectie.nume(UPDATED_NUME);
        SectieDTO sectieDTO = sectieMapper.toDto(updatedSectie);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sectieDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSectieToMatchAllProperties(updatedSectie);
    }

    @Test
    void putNonExistingSectie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sectie.setId(longCount.incrementAndGet());

        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sectieDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSectie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sectie.setId(longCount.incrementAndGet());

        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSectie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sectie.setId(longCount.incrementAndGet());

        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSectieWithPatch() throws Exception {
        // Initialize the database
        insertedSectie = sectieRepository.save(sectie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sectie using partial update
        Sectie partialUpdatedSectie = new Sectie();
        partialUpdatedSectie.setId(sectie.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSectie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSectie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sectie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSectieUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedSectie, sectie), getPersistedSectie(sectie));
    }

    @Test
    void fullUpdateSectieWithPatch() throws Exception {
        // Initialize the database
        insertedSectie = sectieRepository.save(sectie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sectie using partial update
        Sectie partialUpdatedSectie = new Sectie();
        partialUpdatedSectie.setId(sectie.getId());

        partialUpdatedSectie.nume(UPDATED_NUME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSectie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSectie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sectie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSectieUpdatableFieldsEquals(partialUpdatedSectie, getPersistedSectie(partialUpdatedSectie));
    }

    @Test
    void patchNonExistingSectie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sectie.setId(longCount.incrementAndGet());

        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, sectieDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSectie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sectie.setId(longCount.incrementAndGet());

        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSectie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sectie.setId(longCount.incrementAndGet());

        // Create the Sectie
        SectieDTO sectieDTO = sectieMapper.toDto(sectie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(sectieDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Sectie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSectie() {
        // Initialize the database
        insertedSectie = sectieRepository.save(sectie).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sectie
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, sectie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sectieRepository.count().block();
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

    protected Sectie getPersistedSectie(Sectie sectie) {
        return sectieRepository.findById(sectie.getId()).block();
    }

    protected void assertPersistedSectieToMatchAllProperties(Sectie expectedSectie) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSectieAllPropertiesEquals(expectedSectie, getPersistedSectie(expectedSectie));
        assertSectieUpdatableFieldsEquals(expectedSectie, getPersistedSectie(expectedSectie));
    }

    protected void assertPersistedSectieToMatchUpdatableProperties(Sectie expectedSectie) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSectieAllUpdatablePropertiesEquals(expectedSectie, getPersistedSectie(expectedSectie));
        assertSectieUpdatableFieldsEquals(expectedSectie, getPersistedSectie(expectedSectie));
    }
}
