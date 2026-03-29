package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.domain.Specializare;
import com.mycompany.myapp.repository.rowmapper.MedicRowMapper;
import com.mycompany.myapp.repository.rowmapper.SectieRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Medic entity.
 */
@SuppressWarnings("unused")
class MedicRepositoryInternalImpl extends SimpleR2dbcRepository<Medic, Long> implements MedicRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final SectieRowMapper sectieMapper;
    private final MedicRowMapper medicMapper;

    private static final Table entityTable = Table.aliased("medic", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table sectieTable = Table.aliased("sectie", "sectie");

    private static final EntityManager.LinkTable cliniciLink = new EntityManager.LinkTable("rel_medic__clinici", "medic_id", "clinici_id");
    private static final EntityManager.LinkTable specializariLink = new EntityManager.LinkTable(
        "rel_medic__specializari",
        "medic_id",
        "specializari_id"
    );

    public MedicRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        SectieRowMapper sectieMapper,
        MedicRowMapper medicMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Medic.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.sectieMapper = sectieMapper;
        this.medicMapper = medicMapper;
    }

    @Override
    public Flux<Medic> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Medic> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = MedicSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(SectieSqlHelper.getColumns(sectieTable, "sectie"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(sectieTable)
            .on(Column.create("sectie_id", entityTable))
            .equals(Column.create("id", sectieTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Medic.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Medic> findAll() {
        return findAllBy(null);
    }

    @Override
    public Flux<Medic> findByCascadeFilter(Long clinicaId, Long sectieId) {
        String baseSql = "e.id IN (SELECT medic_id FROM rel_medic__clinici WHERE clinici_id = " + clinicaId + ")";
        if (sectieId != null) {
            baseSql += " AND e.sectie_id = " + sectieId;
        }
        Condition condition = Conditions.just(baseSql);
        return createQuery(null, condition).all();
    }

    @Override
    public Mono<Medic> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Medic> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Medic> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Medic> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Medic process(Row row, RowMetadata metadata) {
        Medic entity = medicMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setSectie(sectieMapper.apply(row, "sectie"));
        return entity;
    }

    @Override
    public <S extends Medic> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Medic> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(cliniciLink, entity.getId(), entity.getClinicis().stream().map(Clinica::getId))
            .then();
        result = result.and(
            entityManager.updateLinkTable(specializariLink, entity.getId(), entity.getSpecializaris().stream().map(Specializare::getId))
        );
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(cliniciLink, entityId).and(entityManager.deleteFromLinkTable(specializariLink, entityId));
    }
}
