package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.LocatieAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Locatie;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.LocatieRepository;
import com.mycompany.myapp.service.dto.LocatieDTO;
import com.mycompany.myapp.service.mapper.LocatieMapper;
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
 * Integration tests for the {@link LocatieResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class LocatieResourceIT {

    private static final String DEFAULT_ORAS = "AAAAAAAAAA";
    private static final String UPDATED_ORAS = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESA = "AAAAAAAAAA";
    private static final String UPDATED_ADRESA = "BBBBBBBBBB";

    private static final String DEFAULT_COD_POSTAL = "AAAAAAAAAA";
    private static final String UPDATED_COD_POSTAL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/locaties";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocatieRepository locatieRepository;

    @Autowired
    private LocatieMapper locatieMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Locatie locatie;

    private Locatie insertedLocatie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locatie createEntity() {
        return new Locatie().oras(DEFAULT_ORAS).adresa(DEFAULT_ADRESA).codPostal(DEFAULT_COD_POSTAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locatie createUpdatedEntity() {
        return new Locatie().oras(UPDATED_ORAS).adresa(UPDATED_ADRESA).codPostal(UPDATED_COD_POSTAL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Locatie.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        locatie = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLocatie != null) {
            locatieRepository.delete(insertedLocatie).block();
            insertedLocatie = null;
        }
        deleteEntities(em);
    }

    @Test
    void createLocatie() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);
        var returnedLocatieDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(LocatieDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Locatie in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLocatie = locatieMapper.toEntity(returnedLocatieDTO);
        assertLocatieUpdatableFieldsEquals(returnedLocatie, getPersistedLocatie(returnedLocatie));

        insertedLocatie = returnedLocatie;
    }

    @Test
    void createLocatieWithExistingId() throws Exception {
        // Create the Locatie with an existing ID
        locatie.setId(1L);
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllLocaties() {
        // Initialize the database
        insertedLocatie = locatieRepository.save(locatie).block();

        // Get all the locatieList
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
            .value(hasItem(locatie.getId().intValue()))
            .jsonPath("$.[*].oras")
            .value(hasItem(DEFAULT_ORAS))
            .jsonPath("$.[*].adresa")
            .value(hasItem(DEFAULT_ADRESA))
            .jsonPath("$.[*].codPostal")
            .value(hasItem(DEFAULT_COD_POSTAL));
    }

    @Test
    void getLocatie() {
        // Initialize the database
        insertedLocatie = locatieRepository.save(locatie).block();

        // Get the locatie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, locatie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(locatie.getId().intValue()))
            .jsonPath("$.oras")
            .value(is(DEFAULT_ORAS))
            .jsonPath("$.adresa")
            .value(is(DEFAULT_ADRESA))
            .jsonPath("$.codPostal")
            .value(is(DEFAULT_COD_POSTAL));
    }

    @Test
    void getNonExistingLocatie() {
        // Get the locatie
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingLocatie() throws Exception {
        // Initialize the database
        insertedLocatie = locatieRepository.save(locatie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locatie
        Locatie updatedLocatie = locatieRepository.findById(locatie.getId()).block();
        updatedLocatie.oras(UPDATED_ORAS).adresa(UPDATED_ADRESA).codPostal(UPDATED_COD_POSTAL);
        LocatieDTO locatieDTO = locatieMapper.toDto(updatedLocatie);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, locatieDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocatieToMatchAllProperties(updatedLocatie);
    }

    @Test
    void putNonExistingLocatie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locatie.setId(longCount.incrementAndGet());

        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, locatieDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLocatie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locatie.setId(longCount.incrementAndGet());

        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLocatie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locatie.setId(longCount.incrementAndGet());

        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLocatieWithPatch() throws Exception {
        // Initialize the database
        insertedLocatie = locatieRepository.save(locatie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locatie using partial update
        Locatie partialUpdatedLocatie = new Locatie();
        partialUpdatedLocatie.setId(locatie.getId());

        partialUpdatedLocatie.oras(UPDATED_ORAS).codPostal(UPDATED_COD_POSTAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLocatie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLocatie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Locatie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocatieUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLocatie, locatie), getPersistedLocatie(locatie));
    }

    @Test
    void fullUpdateLocatieWithPatch() throws Exception {
        // Initialize the database
        insertedLocatie = locatieRepository.save(locatie).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locatie using partial update
        Locatie partialUpdatedLocatie = new Locatie();
        partialUpdatedLocatie.setId(locatie.getId());

        partialUpdatedLocatie.oras(UPDATED_ORAS).adresa(UPDATED_ADRESA).codPostal(UPDATED_COD_POSTAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLocatie.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLocatie))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Locatie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocatieUpdatableFieldsEquals(partialUpdatedLocatie, getPersistedLocatie(partialUpdatedLocatie));
    }

    @Test
    void patchNonExistingLocatie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locatie.setId(longCount.incrementAndGet());

        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, locatieDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLocatie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locatie.setId(longCount.incrementAndGet());

        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLocatie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locatie.setId(longCount.incrementAndGet());

        // Create the Locatie
        LocatieDTO locatieDTO = locatieMapper.toDto(locatie);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(locatieDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Locatie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLocatie() {
        // Initialize the database
        insertedLocatie = locatieRepository.save(locatie).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the locatie
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, locatie.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return locatieRepository.count().block();
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

    protected Locatie getPersistedLocatie(Locatie locatie) {
        return locatieRepository.findById(locatie.getId()).block();
    }

    protected void assertPersistedLocatieToMatchAllProperties(Locatie expectedLocatie) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLocatieAllPropertiesEquals(expectedLocatie, getPersistedLocatie(expectedLocatie));
        assertLocatieUpdatableFieldsEquals(expectedLocatie, getPersistedLocatie(expectedLocatie));
    }

    protected void assertPersistedLocatieToMatchUpdatableProperties(Locatie expectedLocatie) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLocatieAllUpdatablePropertiesEquals(expectedLocatie, getPersistedLocatie(expectedLocatie));
        assertLocatieUpdatableFieldsEquals(expectedLocatie, getPersistedLocatie(expectedLocatie));
    }
}
