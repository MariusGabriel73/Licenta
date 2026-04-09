package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PacientAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Pacient;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.PacientRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.dto.PacientDTO;
import com.mycompany.myapp.service.mapper.PacientMapper;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PacientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PacientResourceIT {

    private static final String DEFAULT_CNP = "AAAAAAAAAA";
    private static final String UPDATED_CNP = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFON = "AAAAAAAAAA";
    private static final String UPDATED_TELEFON = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASTERII = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASTERII = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_ADRESA = "AAAAAAAAAA";
    private static final String UPDATED_ADRESA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pacients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PacientRepository pacientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PacientMapper pacientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pacient pacient;

    private Pacient insertedPacient;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pacient createEntity() {
        return new Pacient().cnp(DEFAULT_CNP).telefon(DEFAULT_TELEFON).dataNasterii(DEFAULT_DATA_NASTERII).adresa(DEFAULT_ADRESA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pacient createUpdatedEntity() {
        return new Pacient().cnp(UPDATED_CNP).telefon(UPDATED_TELEFON).dataNasterii(UPDATED_DATA_NASTERII).adresa(UPDATED_ADRESA);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pacient.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        pacient = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPacient != null) {
            pacientRepository.delete(insertedPacient).block();
            insertedPacient = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createPacient() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);
        var returnedPacientDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(PacientDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Pacient in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPacient = pacientMapper.toEntity(returnedPacientDTO);
        assertPacientUpdatableFieldsEquals(returnedPacient, getPersistedPacient(returnedPacient));

        insertedPacient = returnedPacient;
    }

    @Test
    void createPacientWithExistingId() throws Exception {
        // Create the Pacient with an existing ID
        pacient.setId(1L);
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkCnpIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pacient.setCnp(null);

        // Create the Pacient, which fails.
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPacients() {
        // Initialize the database
        insertedPacient = pacientRepository.save(pacient).block();

        // Get all the pacientList
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
            .value(hasItem(pacient.getId().intValue()))
            .jsonPath("$.[*].cnp")
            .value(hasItem(DEFAULT_CNP))
            .jsonPath("$.[*].telefon")
            .value(hasItem(DEFAULT_TELEFON))
            .jsonPath("$.[*].dataNasterii")
            .value(hasItem(DEFAULT_DATA_NASTERII.toString()))
            .jsonPath("$.[*].adresa")
            .value(hasItem(DEFAULT_ADRESA));
    }

    @Test
    void getPacient() {
        // Initialize the database
        insertedPacient = pacientRepository.save(pacient).block();

        // Get the pacient
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pacient.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pacient.getId().intValue()))
            .jsonPath("$.cnp")
            .value(is(DEFAULT_CNP))
            .jsonPath("$.telefon")
            .value(is(DEFAULT_TELEFON))
            .jsonPath("$.dataNasterii")
            .value(is(DEFAULT_DATA_NASTERII.toString()))
            .jsonPath("$.adresa")
            .value(is(DEFAULT_ADRESA));
    }

    @Test
    void getNonExistingPacient() {
        // Get the pacient
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingPacient() throws Exception {
        // Initialize the database
        insertedPacient = pacientRepository.save(pacient).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pacient
        Pacient updatedPacient = pacientRepository.findById(pacient.getId()).block();
        updatedPacient.cnp(UPDATED_CNP).telefon(UPDATED_TELEFON).dataNasterii(UPDATED_DATA_NASTERII).adresa(UPDATED_ADRESA);
        PacientDTO pacientDTO = pacientMapper.toDto(updatedPacient);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pacientDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPacientToMatchAllProperties(updatedPacient);
    }

    @Test
    void putNonExistingPacient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pacient.setId(longCount.incrementAndGet());

        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pacientDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPacient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pacient.setId(longCount.incrementAndGet());

        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPacient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pacient.setId(longCount.incrementAndGet());

        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePacientWithPatch() throws Exception {
        // Initialize the database
        insertedPacient = pacientRepository.save(pacient).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pacient using partial update
        Pacient partialUpdatedPacient = new Pacient();
        partialUpdatedPacient.setId(pacient.getId());

        partialUpdatedPacient.telefon(UPDATED_TELEFON).dataNasterii(UPDATED_DATA_NASTERII);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPacient.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPacient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pacient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPacientUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPacient, pacient), getPersistedPacient(pacient));
    }

    @Test
    void fullUpdatePacientWithPatch() throws Exception {
        // Initialize the database
        insertedPacient = pacientRepository.save(pacient).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pacient using partial update
        Pacient partialUpdatedPacient = new Pacient();
        partialUpdatedPacient.setId(pacient.getId());

        partialUpdatedPacient.cnp(UPDATED_CNP).telefon(UPDATED_TELEFON).dataNasterii(UPDATED_DATA_NASTERII).adresa(UPDATED_ADRESA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPacient.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedPacient))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pacient in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPacientUpdatableFieldsEquals(partialUpdatedPacient, getPersistedPacient(partialUpdatedPacient));
    }

    @Test
    void patchNonExistingPacient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pacient.setId(longCount.incrementAndGet());

        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pacientDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPacient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pacient.setId(longCount.incrementAndGet());

        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPacient() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pacient.setId(longCount.incrementAndGet());

        // Create the Pacient
        PacientDTO pacientDTO = pacientMapper.toDto(pacient);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(pacientDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pacient in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePacient() {
        // Initialize the database
        insertedPacient = pacientRepository.save(pacient).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pacient
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pacient.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pacientRepository.count().block();
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

    protected Pacient getPersistedPacient(Pacient pacient) {
        return pacientRepository.findById(pacient.getId()).block();
    }

    protected void assertPersistedPacientToMatchAllProperties(Pacient expectedPacient) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPacientAllPropertiesEquals(expectedPacient, getPersistedPacient(expectedPacient));
        assertPacientUpdatableFieldsEquals(expectedPacient, getPersistedPacient(expectedPacient));
    }

    protected void assertPersistedPacientToMatchUpdatableProperties(Pacient expectedPacient) {
        // Test fails because reactive api returns an empty object instead of null
        // assertPacientAllUpdatablePropertiesEquals(expectedPacient, getPersistedPacient(expectedPacient));
        assertPacientUpdatableFieldsEquals(expectedPacient, getPersistedPacient(expectedPacient));
    }
}
