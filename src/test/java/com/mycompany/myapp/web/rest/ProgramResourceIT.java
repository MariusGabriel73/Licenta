package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.ProgramAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Program;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.ProgramRepository;
import com.mycompany.myapp.service.dto.ProgramDTO;
import com.mycompany.myapp.service.mapper.ProgramMapper;
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
 * Integration tests for the {@link ProgramResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProgramResourceIT {

    private static final String DEFAULT_ZIUA_SAPTAMANII = "AAAAAAAAAA";
    private static final String UPDATED_ZIUA_SAPTAMANII = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_ORA_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ORA_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_ORA_FINAL = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ORA_FINAL = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/programs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Program program;

    private Program insertedProgram;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Program createEntity() {
        return new Program().ziuaSaptamanii(DEFAULT_ZIUA_SAPTAMANII).oraStart(DEFAULT_ORA_START).oraFinal(DEFAULT_ORA_FINAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Program createUpdatedEntity() {
        return new Program().ziuaSaptamanii(UPDATED_ZIUA_SAPTAMANII).oraStart(UPDATED_ORA_START).oraFinal(UPDATED_ORA_FINAL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Program.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        program = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProgram != null) {
            programRepository.delete(insertedProgram).block();
            insertedProgram = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProgram() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);
        var returnedProgramDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProgramDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Program in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProgram = programMapper.toEntity(returnedProgramDTO);
        assertProgramUpdatableFieldsEquals(returnedProgram, getPersistedProgram(returnedProgram));

        insertedProgram = returnedProgram;
    }

    @Test
    void createProgramWithExistingId() throws Exception {
        // Create the Program with an existing ID
        program.setId(1L);
        ProgramDTO programDTO = programMapper.toDto(program);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPrograms() {
        // Initialize the database
        insertedProgram = programRepository.save(program).block();

        // Get all the programList
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
            .value(hasItem(program.getId().intValue()))
            .jsonPath("$.[*].ziuaSaptamanii")
            .value(hasItem(DEFAULT_ZIUA_SAPTAMANII))
            .jsonPath("$.[*].oraStart")
            .value(hasItem(sameInstant(DEFAULT_ORA_START)))
            .jsonPath("$.[*].oraFinal")
            .value(hasItem(sameInstant(DEFAULT_ORA_FINAL)));
    }

    @Test
    void getProgram() {
        // Initialize the database
        insertedProgram = programRepository.save(program).block();

        // Get the program
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, program.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(program.getId().intValue()))
            .jsonPath("$.ziuaSaptamanii")
            .value(is(DEFAULT_ZIUA_SAPTAMANII))
            .jsonPath("$.oraStart")
            .value(is(sameInstant(DEFAULT_ORA_START)))
            .jsonPath("$.oraFinal")
            .value(is(sameInstant(DEFAULT_ORA_FINAL)));
    }

    @Test
    void getNonExistingProgram() {
        // Get the program
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProgram() throws Exception {
        // Initialize the database
        insertedProgram = programRepository.save(program).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the program
        Program updatedProgram = programRepository.findById(program.getId()).block();
        updatedProgram.ziuaSaptamanii(UPDATED_ZIUA_SAPTAMANII).oraStart(UPDATED_ORA_START).oraFinal(UPDATED_ORA_FINAL);
        ProgramDTO programDTO = programMapper.toDto(updatedProgram);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, programDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProgramToMatchAllProperties(updatedProgram);
    }

    @Test
    void putNonExistingProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        program.setId(longCount.incrementAndGet());

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, programDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        program.setId(longCount.incrementAndGet());

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        program.setId(longCount.incrementAndGet());

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProgramWithPatch() throws Exception {
        // Initialize the database
        insertedProgram = programRepository.save(program).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the program using partial update
        Program partialUpdatedProgram = new Program();
        partialUpdatedProgram.setId(program.getId());

        partialUpdatedProgram.ziuaSaptamanii(UPDATED_ZIUA_SAPTAMANII);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProgram.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProgram))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Program in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProgramUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProgram, program), getPersistedProgram(program));
    }

    @Test
    void fullUpdateProgramWithPatch() throws Exception {
        // Initialize the database
        insertedProgram = programRepository.save(program).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the program using partial update
        Program partialUpdatedProgram = new Program();
        partialUpdatedProgram.setId(program.getId());

        partialUpdatedProgram.ziuaSaptamanii(UPDATED_ZIUA_SAPTAMANII).oraStart(UPDATED_ORA_START).oraFinal(UPDATED_ORA_FINAL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProgram.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProgram))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Program in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProgramUpdatableFieldsEquals(partialUpdatedProgram, getPersistedProgram(partialUpdatedProgram));
    }

    @Test
    void patchNonExistingProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        program.setId(longCount.incrementAndGet());

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, programDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        program.setId(longCount.incrementAndGet());

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProgram() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        program.setId(longCount.incrementAndGet());

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(programDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Program in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProgram() {
        // Initialize the database
        insertedProgram = programRepository.save(program).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the program
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, program.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return programRepository.count().block();
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

    protected Program getPersistedProgram(Program program) {
        return programRepository.findById(program.getId()).block();
    }

    protected void assertPersistedProgramToMatchAllProperties(Program expectedProgram) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProgramAllPropertiesEquals(expectedProgram, getPersistedProgram(expectedProgram));
        assertProgramUpdatableFieldsEquals(expectedProgram, getPersistedProgram(expectedProgram));
    }

    protected void assertPersistedProgramToMatchUpdatableProperties(Program expectedProgram) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProgramAllUpdatablePropertiesEquals(expectedProgram, getPersistedProgram(expectedProgram));
        assertProgramUpdatableFieldsEquals(expectedProgram, getPersistedProgram(expectedProgram));
    }
}
