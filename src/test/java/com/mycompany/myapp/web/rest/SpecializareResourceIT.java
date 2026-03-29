package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.SpecializareAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Specializare;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.SpecializareRepository;
import com.mycompany.myapp.service.dto.SpecializareDTO;
import com.mycompany.myapp.service.mapper.SpecializareMapper;
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
 * Integration tests for the {@link SpecializareResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SpecializareResourceIT {

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/specializares";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SpecializareRepository specializareRepository;

    @Autowired
    private SpecializareMapper specializareMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Specializare specializare;

    private Specializare insertedSpecializare;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specializare createEntity() {
        return new Specializare().nume(DEFAULT_NUME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Specializare createUpdatedEntity() {
        return new Specializare().nume(UPDATED_NUME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Specializare.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        specializare = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSpecializare != null) {
            specializareRepository.delete(insertedSpecializare).block();
            insertedSpecializare = null;
        }
        deleteEntities(em);
    }

    @Test
    void createSpecializare() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);
        var returnedSpecializareDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(SpecializareDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Specializare in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSpecializare = specializareMapper.toEntity(returnedSpecializareDTO);
        assertSpecializareUpdatableFieldsEquals(returnedSpecializare, getPersistedSpecializare(returnedSpecializare));

        insertedSpecializare = returnedSpecializare;
    }

    @Test
    void createSpecializareWithExistingId() throws Exception {
        // Create the Specializare with an existing ID
        specializare.setId(1L);
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNumeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        specializare.setNume(null);

        // Create the Specializare, which fails.
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSpecializares() {
        // Initialize the database
        insertedSpecializare = specializareRepository.save(specializare).block();

        // Get all the specializareList
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
            .value(hasItem(specializare.getId().intValue()))
            .jsonPath("$.[*].nume")
            .value(hasItem(DEFAULT_NUME));
    }

    @Test
    void getSpecializare() {
        // Initialize the database
        insertedSpecializare = specializareRepository.save(specializare).block();

        // Get the specializare
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, specializare.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(specializare.getId().intValue()))
            .jsonPath("$.nume")
            .value(is(DEFAULT_NUME));
    }

    @Test
    void getNonExistingSpecializare() {
        // Get the specializare
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingSpecializare() throws Exception {
        // Initialize the database
        insertedSpecializare = specializareRepository.save(specializare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the specializare
        Specializare updatedSpecializare = specializareRepository.findById(specializare.getId()).block();
        updatedSpecializare.nume(UPDATED_NUME);
        SpecializareDTO specializareDTO = specializareMapper.toDto(updatedSpecializare);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, specializareDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSpecializareToMatchAllProperties(updatedSpecializare);
    }

    @Test
    void putNonExistingSpecializare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializare.setId(longCount.incrementAndGet());

        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, specializareDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSpecializare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializare.setId(longCount.incrementAndGet());

        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSpecializare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializare.setId(longCount.incrementAndGet());

        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSpecializareWithPatch() throws Exception {
        // Initialize the database
        insertedSpecializare = specializareRepository.save(specializare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the specializare using partial update
        Specializare partialUpdatedSpecializare = new Specializare();
        partialUpdatedSpecializare.setId(specializare.getId());

        partialUpdatedSpecializare.nume(UPDATED_NUME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSpecializare.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSpecializare))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Specializare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpecializareUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSpecializare, specializare),
            getPersistedSpecializare(specializare)
        );
    }

    @Test
    void fullUpdateSpecializareWithPatch() throws Exception {
        // Initialize the database
        insertedSpecializare = specializareRepository.save(specializare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the specializare using partial update
        Specializare partialUpdatedSpecializare = new Specializare();
        partialUpdatedSpecializare.setId(specializare.getId());

        partialUpdatedSpecializare.nume(UPDATED_NUME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSpecializare.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedSpecializare))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Specializare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSpecializareUpdatableFieldsEquals(partialUpdatedSpecializare, getPersistedSpecializare(partialUpdatedSpecializare));
    }

    @Test
    void patchNonExistingSpecializare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializare.setId(longCount.incrementAndGet());

        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, specializareDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSpecializare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializare.setId(longCount.incrementAndGet());

        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSpecializare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        specializare.setId(longCount.incrementAndGet());

        // Create the Specializare
        SpecializareDTO specializareDTO = specializareMapper.toDto(specializare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(specializareDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Specializare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSpecializare() {
        // Initialize the database
        insertedSpecializare = specializareRepository.save(specializare).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the specializare
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, specializare.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return specializareRepository.count().block();
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

    protected Specializare getPersistedSpecializare(Specializare specializare) {
        return specializareRepository.findById(specializare.getId()).block();
    }

    protected void assertPersistedSpecializareToMatchAllProperties(Specializare expectedSpecializare) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSpecializareAllPropertiesEquals(expectedSpecializare, getPersistedSpecializare(expectedSpecializare));
        assertSpecializareUpdatableFieldsEquals(expectedSpecializare, getPersistedSpecializare(expectedSpecializare));
    }

    protected void assertPersistedSpecializareToMatchUpdatableProperties(Specializare expectedSpecializare) {
        // Test fails because reactive api returns an empty object instead of null
        // assertSpecializareAllUpdatablePropertiesEquals(expectedSpecializare, getPersistedSpecializare(expectedSpecializare));
        assertSpecializareUpdatableFieldsEquals(expectedSpecializare, getPersistedSpecializare(expectedSpecializare));
    }
}
