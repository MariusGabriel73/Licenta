package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ProgramareAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.domain.enumeration.ProgramareStatus;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProgramareRepository;
import com.mycompany.myapp.service.dto.ProgramareDTO;
import com.mycompany.myapp.service.mapper.ProgramareMapper;
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
 * Integration tests for the {@link ProgramareResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProgramareResourceIT {

    private static final ZonedDateTime DEFAULT_DATA_PROGRAMARE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_PROGRAMARE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ProgramareStatus DEFAULT_STATUS = ProgramareStatus.ACTIVA;
    private static final ProgramareStatus UPDATED_STATUS = ProgramareStatus.ANULATA;

    private static final String DEFAULT_OBSERVATII = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVATII = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/programares";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProgramareRepository programareRepository;

    @Autowired
    private ProgramareMapper programareMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Programare programare;

    private Programare insertedProgramare;

    /**
     * Create an entity for this test.
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Programare createEntity() {
        return new Programare().dataProgramare(DEFAULT_DATA_PROGRAMARE).status(DEFAULT_STATUS).observatii(DEFAULT_OBSERVATII);
    }

    /**
     * Create an updated entity for this test.
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Programare createUpdatedEntity() {
        return new Programare().dataProgramare(UPDATED_DATA_PROGRAMARE).status(UPDATED_STATUS).observatii(UPDATED_OBSERVATII);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Programare.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        programare = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProgramare != null) {
            programareRepository.delete(insertedProgramare).block();
            insertedProgramare = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProgramare() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);
        var returnedProgramareDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProgramareDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Programare in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProgramare = programareMapper.toEntity(returnedProgramareDTO);
        assertProgramareUpdatableFieldsEquals(returnedProgramare, getPersistedProgramare(returnedProgramare));

        insertedProgramare = returnedProgramare;
    }

    @Test
    void createProgramareWithExistingId() throws Exception {
        // Create the Programare with an existing ID
        programare.setId(1L);
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProgramares() {
        // Initialize the database
        insertedProgramare = programareRepository.save(programare).block();

        // Get all the programareList
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
            .value(hasItem(programare.getId().intValue()))
            .jsonPath("$.[*].dataProgramare")
            .value(hasItem(sameInstant(DEFAULT_DATA_PROGRAMARE)))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].observatii")
            .value(hasItem(DEFAULT_OBSERVATII));
    }

    @Test
    void getProgramare() {
        // Initialize the database
        insertedProgramare = programareRepository.save(programare).block();

        // Get the programare
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, programare.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(programare.getId().intValue()))
            .jsonPath("$.dataProgramare")
            .value(is(sameInstant(DEFAULT_DATA_PROGRAMARE)))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.observatii")
            .value(is(DEFAULT_OBSERVATII));
    }

    @Test
    void getNonExistingProgramare() {
        // Get the programare
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProgramare() throws Exception {
        // Initialize the database
        insertedProgramare = programareRepository.save(programare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the programare
        Programare updatedProgramare = programareRepository.findById(programare.getId()).block();
        java.util.Objects.requireNonNull(updatedProgramare);
        updatedProgramare.dataProgramare(UPDATED_DATA_PROGRAMARE).status(UPDATED_STATUS).observatii(UPDATED_OBSERVATII);
        ProgramareDTO programareDTO = programareMapper.toDto(updatedProgramare);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, programareDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProgramareToMatchAllProperties(updatedProgramare);
    }

    @Test
    void putNonExistingProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        programare.setId(longCount.incrementAndGet());

        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, programareDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        programare.setId(longCount.incrementAndGet());

        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        programare.setId(longCount.incrementAndGet());

        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProgramareWithPatch() throws Exception {
        // Initialize the database
        insertedProgramare = programareRepository.save(programare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the programare using partial update
        Programare partialUpdatedProgramare = new Programare();
        partialUpdatedProgramare.setId(programare.getId());

        partialUpdatedProgramare.status(UPDATED_STATUS).observatii(UPDATED_OBSERVATII);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProgramare.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProgramare))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Programare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProgramareUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProgramare, programare),
            getPersistedProgramare(programare)
        );
    }

    @Test
    void fullUpdateProgramareWithPatch() throws Exception {
        // Initialize the database
        insertedProgramare = programareRepository.save(programare).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the programare using partial update
        Programare partialUpdatedProgramare = new Programare();
        partialUpdatedProgramare.setId(programare.getId());

        partialUpdatedProgramare.dataProgramare(UPDATED_DATA_PROGRAMARE).status(UPDATED_STATUS).observatii(UPDATED_OBSERVATII);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProgramare.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProgramare))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Programare in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProgramareUpdatableFieldsEquals(partialUpdatedProgramare, getPersistedProgramare(partialUpdatedProgramare));
    }

    @Test
    void patchNonExistingProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        programare.setId(longCount.incrementAndGet());

        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, programareDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        programare.setId(longCount.incrementAndGet());

        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProgramare() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        programare.setId(longCount.incrementAndGet());

        // Create the Programare
        ProgramareDTO programareDTO = programareMapper.toDto(programare);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(programareDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Programare in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProgramare() {
        // Initialize the database
        insertedProgramare = programareRepository.save(programare).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the programare
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, programare.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        Long count = programareRepository.count().block();
        return count != null ? count : 0L;
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

    protected Programare getPersistedProgramare(Programare programare) {
        return programareRepository.findById(programare.getId()).block();
    }

    protected void assertPersistedProgramareToMatchAllProperties(Programare expectedProgramare) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProgramareAllPropertiesEquals(expectedProgramare, getPersistedProgramare(expectedProgramare));
        assertProgramareUpdatableFieldsEquals(expectedProgramare, getPersistedProgramare(expectedProgramare));
    }

    @SuppressWarnings("unused")
    protected void assertPersistedProgramareToMatchUpdatableProperties(Programare expectedProgramare) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProgramareAllUpdatablePropertiesEquals(expectedProgramare, getPersistedProgramare(expectedProgramare));
        assertProgramareUpdatableFieldsEquals(expectedProgramare, getPersistedProgramare(expectedProgramare));
    }
}
