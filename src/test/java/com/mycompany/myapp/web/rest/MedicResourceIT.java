package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.MedicAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.repository.MedicRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.MedicService;
import com.mycompany.myapp.service.dto.MedicDTO;
import com.mycompany.myapp.service.mapper.MedicMapper;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link MedicResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MedicResourceIT {

    private static final String DEFAULT_GRAD_PROFESIONAL = "AAAAAAAAAA";
    private static final String UPDATED_GRAD_PROFESIONAL = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFON = "AAAAAAAAAA";
    private static final String UPDATED_TELEFON = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DISPONIBIL = false;
    private static final Boolean UPDATED_DISPONIBIL = true;

    private static final String ENTITY_API_URL = "/api/medics";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MedicRepository medicRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private MedicRepository medicRepositoryMock;

    @Autowired
    private MedicMapper medicMapper;

    @Mock
    private MedicService medicServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Medic medic;

    private Medic insertedMedic;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medic createEntity() {
        return new Medic().gradProfesional(DEFAULT_GRAD_PROFESIONAL).telefon(DEFAULT_TELEFON).disponibil(DEFAULT_DISPONIBIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Medic createUpdatedEntity() {
        return new Medic().gradProfesional(UPDATED_GRAD_PROFESIONAL).telefon(UPDATED_TELEFON).disponibil(UPDATED_DISPONIBIL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_medic__clinici").block();
            em.deleteAll("rel_medic__specializari").block();
            em.deleteAll(Medic.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    void initTest() {
        medic = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMedic != null) {
            medicRepository.delete(insertedMedic).block();
            insertedMedic = null;
        }
        deleteEntities(em);
        userRepository.deleteAllUserAuthorities().block();
        userRepository.deleteAll().block();
    }

    @Test
    void createMedic() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);
        var returnedMedicDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MedicDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Medic in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMedic = medicMapper.toEntity(returnedMedicDTO);
        assertMedicUpdatableFieldsEquals(returnedMedic, getPersistedMedic(returnedMedic));

        insertedMedic = returnedMedic;
    }

    @Test
    void createMedicWithExistingId() throws Exception {
        // Create the Medic with an existing ID
        medic.setId(1L);
        MedicDTO medicDTO = medicMapper.toDto(medic);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMedics() {
        // Initialize the database
        insertedMedic = medicRepository.save(medic).block();

        // Get all the medicList
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
            .value(hasItem(medic.getId().intValue()))
            .jsonPath("$.[*].gradProfesional")
            .value(hasItem(DEFAULT_GRAD_PROFESIONAL))
            .jsonPath("$.[*].telefon")
            .value(hasItem(DEFAULT_TELEFON))
            .jsonPath("$.[*].disponibil")
            .value(hasItem(DEFAULT_DISPONIBIL));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMedicsWithEagerRelationshipsIsEnabled() {
        when(medicServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(medicServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMedicsWithEagerRelationshipsIsNotEnabled() {
        when(medicServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(medicRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getMedic() {
        // Initialize the database
        insertedMedic = medicRepository.save(medic).block();

        // Get the medic
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, medic.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(medic.getId().intValue()))
            .jsonPath("$.gradProfesional")
            .value(is(DEFAULT_GRAD_PROFESIONAL))
            .jsonPath("$.telefon")
            .value(is(DEFAULT_TELEFON))
            .jsonPath("$.disponibil")
            .value(is(DEFAULT_DISPONIBIL));
    }

    @Test
    void getNonExistingMedic() {
        // Get the medic
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingMedic() throws Exception {
        // Initialize the database
        insertedMedic = medicRepository.save(medic).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medic
        Medic updatedMedic = medicRepository.findById(medic.getId()).block();
        updatedMedic.gradProfesional(UPDATED_GRAD_PROFESIONAL).telefon(UPDATED_TELEFON).disponibil(UPDATED_DISPONIBIL);
        MedicDTO medicDTO = medicMapper.toDto(updatedMedic);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, medicDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMedicToMatchAllProperties(updatedMedic);
    }

    @Test
    void putNonExistingMedic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medic.setId(longCount.incrementAndGet());

        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, medicDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMedic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medic.setId(longCount.incrementAndGet());

        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMedic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medic.setId(longCount.incrementAndGet());

        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMedicWithPatch() throws Exception {
        // Initialize the database
        insertedMedic = medicRepository.save(medic).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medic using partial update
        Medic partialUpdatedMedic = new Medic();
        partialUpdatedMedic.setId(medic.getId());

        partialUpdatedMedic.telefon(UPDATED_TELEFON).disponibil(UPDATED_DISPONIBIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMedic.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMedic))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Medic in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMedicUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMedic, medic), getPersistedMedic(medic));
    }

    @Test
    void fullUpdateMedicWithPatch() throws Exception {
        // Initialize the database
        insertedMedic = medicRepository.save(medic).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the medic using partial update
        Medic partialUpdatedMedic = new Medic();
        partialUpdatedMedic.setId(medic.getId());

        partialUpdatedMedic.gradProfesional(UPDATED_GRAD_PROFESIONAL).telefon(UPDATED_TELEFON).disponibil(UPDATED_DISPONIBIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMedic.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedMedic))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Medic in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMedicUpdatableFieldsEquals(partialUpdatedMedic, getPersistedMedic(partialUpdatedMedic));
    }

    @Test
    void patchNonExistingMedic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medic.setId(longCount.incrementAndGet());

        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, medicDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMedic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medic.setId(longCount.incrementAndGet());

        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMedic() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        medic.setId(longCount.incrementAndGet());

        // Create the Medic
        MedicDTO medicDTO = medicMapper.toDto(medic);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(medicDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Medic in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMedic() {
        // Initialize the database
        insertedMedic = medicRepository.save(medic).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the medic
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, medic.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return medicRepository.count().block();
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

    protected Medic getPersistedMedic(Medic medic) {
        return medicRepository.findById(medic.getId()).block();
    }

    protected void assertPersistedMedicToMatchAllProperties(Medic expectedMedic) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMedicAllPropertiesEquals(expectedMedic, getPersistedMedic(expectedMedic));
        assertMedicUpdatableFieldsEquals(expectedMedic, getPersistedMedic(expectedMedic));
    }

    protected void assertPersistedMedicToMatchUpdatableProperties(Medic expectedMedic) {
        // Test fails because reactive api returns an empty object instead of null
        // assertMedicAllUpdatablePropertiesEquals(expectedMedic, getPersistedMedic(expectedMedic));
        assertMedicUpdatableFieldsEquals(expectedMedic, getPersistedMedic(expectedMedic));
    }
}
