package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.repository.rowmapper.ClinicaRowMapper;
import com.mycompany.myapp.repository.rowmapper.LocatieRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Clinica entity.
 */
@SuppressWarnings("unused")
class ClinicaRepositoryInternalImpl extends SimpleR2dbcRepository<Clinica, Long> implements ClinicaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final LocatieRowMapper locatieMapper;
    private final ClinicaRowMapper clinicaMapper;

    private static final Table entityTable = Table.aliased("clinica", EntityManager.ENTITY_ALIAS);
    private static final Table locatieTable = Table.aliased("locatie", "locatie");

    public ClinicaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        LocatieRowMapper locatieMapper,
        ClinicaRowMapper clinicaMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Clinica.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.locatieMapper = locatieMapper;
        this.clinicaMapper = clinicaMapper;
    }

    @Override
    public Flux<Clinica> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Clinica> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ClinicaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(LocatieSqlHelper.getColumns(locatieTable, "locatie"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(locatieTable)
            .on(Column.create("locatie_id", entityTable))
            .equals(Column.create("id", locatieTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Clinica.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Clinica> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Clinica> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Flux<Clinica> findByLocatie(Long id) {
        Condition whereClause = Conditions.isEqual(entityTable.column("locatie_id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).all();
    }

    private Clinica process(Row row, RowMetadata metadata) {
        Clinica entity = clinicaMapper.apply(row, "e");
        entity.setLocatie(locatieMapper.apply(row, "locatie"));
        return entity;
    }

    @Override
    public <S extends Clinica> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
