package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ClinicaAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.repository.ClinicaRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.ClinicaDTO;
import com.mycompany.myapp.service.mapper.ClinicaMapper;
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
 * Integration tests for the {@link ClinicaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ClinicaResourceIT {

    private static final String DEFAULT_NUME = "AAAAAAAAAA";
    private static final String UPDATED_NUME = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFON = "AAAAAAAAAA";
    private static final String UPDATED_TELEFON = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clinicas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ClinicaRepository clinicaRepository;

    @Autowired
    private ClinicaMapper clinicaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Clinica clinica;

    private Clinica insertedClinica;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clinica createEntity() {
        return new Clinica().nume(DEFAULT_NUME).telefon(DEFAULT_TELEFON).email(DEFAULT_EMAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Clinica createUpdatedEntity() {
        return new Clinica().nume(UPDATED_NUME).telefon(UPDATED_TELEFON).email(UPDATED_EMAIL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Clinica.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        clinica = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedClinica != null) {
            clinicaRepository.delete(insertedClinica).block();
            insertedClinica = null;
        }
        deleteEntities(em);
    }

    @Test
    void createClinica() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);
        var returnedClinicaDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ClinicaDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Clinica in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedClinica = clinicaMapper.toEntity(returnedClinicaDTO);
        assertClinicaUpdatableFieldsEquals(returnedClinica, getPersistedClinica(returnedClinica));

        insertedClinica = returnedClinica;
    }

    @Test
    void createClinicaWithExistingId() throws Exception {
        // Create the Clinica with an existing ID
        clinica.setId(1L);
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNumeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        clinica.setNume(null);

        // Create the Clinica, which fails.
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllClinicas() {
        // Initialize the database
        insertedClinica = clinicaRepository.save(clinica).block();

        // Get all the clinicaList
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
            .value(hasItem(clinica.getId().intValue()))
            .jsonPath("$.[*].nume")
            .value(hasItem(DEFAULT_NUME))
            .jsonPath("$.[*].telefon")
            .value(hasItem(DEFAULT_TELEFON))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @Test
    void getClinica() {
        // Initialize the database
        insertedClinica = clinicaRepository.save(clinica).block();

        // Get the clinica
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, clinica.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(clinica.getId().intValue()))
            .jsonPath("$.nume")
            .value(is(DEFAULT_NUME))
            .jsonPath("$.telefon")
            .value(is(DEFAULT_TELEFON))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingClinica() {
        // Get the clinica
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingClinica() throws Exception {
        // Initialize the database
        insertedClinica = clinicaRepository.save(clinica).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinica
        Clinica updatedClinica = clinicaRepository.findById(clinica.getId()).block();
        updatedClinica.nume(UPDATED_NUME).telefon(UPDATED_TELEFON).email(UPDATED_EMAIL);
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(updatedClinica);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, clinicaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedClinicaToMatchAllProperties(updatedClinica);
    }

    @Test
    void putNonExistingClinica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinica.setId(longCount.incrementAndGet());

        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, clinicaDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchClinica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinica.setId(longCount.incrementAndGet());

        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamClinica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinica.setId(longCount.incrementAndGet());

        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateClinicaWithPatch() throws Exception {
        // Initialize the database
        insertedClinica = clinicaRepository.save(clinica).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinica using partial update
        Clinica partialUpdatedClinica = new Clinica();
        partialUpdatedClinica.setId(clinica.getId());

        partialUpdatedClinica.nume(UPDATED_NUME).telefon(UPDATED_TELEFON).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClinica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClinica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clinica in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClinicaUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedClinica, clinica), getPersistedClinica(clinica));
    }

    @Test
    void fullUpdateClinicaWithPatch() throws Exception {
        // Initialize the database
        insertedClinica = clinicaRepository.save(clinica).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the clinica using partial update
        Clinica partialUpdatedClinica = new Clinica();
        partialUpdatedClinica.setId(clinica.getId());

        partialUpdatedClinica.nume(UPDATED_NUME).telefon(UPDATED_TELEFON).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedClinica.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedClinica))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Clinica in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertClinicaUpdatableFieldsEquals(partialUpdatedClinica, getPersistedClinica(partialUpdatedClinica));
    }

    @Test
    void patchNonExistingClinica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinica.setId(longCount.incrementAndGet());

        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, clinicaDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchClinica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinica.setId(longCount.incrementAndGet());

        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamClinica() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        clinica.setId(longCount.incrementAndGet());

        // Create the Clinica
        ClinicaDTO clinicaDTO = clinicaMapper.toDto(clinica);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(clinicaDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Clinica in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteClinica() {
        // Initialize the database
        insertedClinica = clinicaRepository.save(clinica).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the clinica
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, clinica.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return clinicaRepository.count().block();
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

    protected Clinica getPersistedClinica(Clinica clinica) {
        return clinicaRepository.findById(clinica.getId()).block();
    }

    protected void assertPersistedClinicaToMatchAllProperties(Clinica expectedClinica) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClinicaAllPropertiesEquals(expectedClinica, getPersistedClinica(expectedClinica));
        assertClinicaUpdatableFieldsEquals(expectedClinica, getPersistedClinica(expectedClinica));
    }

    protected void assertPersistedClinicaToMatchUpdatableProperties(Clinica expectedClinica) {
        // Test fails because reactive api returns an empty object instead of null
        // assertClinicaAllUpdatablePropertiesEquals(expectedClinica, getPersistedClinica(expectedClinica));
        assertClinicaUpdatableFieldsEquals(expectedClinica, getPersistedClinica(expectedClinica));
    }
}
