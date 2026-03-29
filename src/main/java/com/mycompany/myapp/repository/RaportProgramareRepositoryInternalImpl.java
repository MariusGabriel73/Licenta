package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.RaportProgramare;
import com.mycompany.myapp.repository.rowmapper.ProgramareRowMapper;
import com.mycompany.myapp.repository.rowmapper.RaportProgramareRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the RaportProgramare entity.
 */
@SuppressWarnings("unused")
class RaportProgramareRepositoryInternalImpl
    extends SimpleR2dbcRepository<RaportProgramare, Long>
    implements RaportProgramareRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProgramareRowMapper programareMapper;
    private final RaportProgramareRowMapper raportprogramareMapper;

    private static final Table entityTable = Table.aliased("raport_programare", EntityManager.ENTITY_ALIAS);
    private static final Table programareTable = Table.aliased("programare", "programare");

    public RaportProgramareRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProgramareRowMapper programareMapper,
        RaportProgramareRowMapper raportprogramareMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(RaportProgramare.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.programareMapper = programareMapper;
        this.raportprogramareMapper = raportprogramareMapper;
    }

    @Override
    public Flux<RaportProgramare> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<RaportProgramare> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RaportProgramareSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProgramareSqlHelper.getColumns(programareTable, "programare"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(programareTable)
            .on(Column.create("programare_id", entityTable))
            .equals(Column.create("id", programareTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, RaportProgramare.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<RaportProgramare> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<RaportProgramare> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private RaportProgramare process(Row row, RowMetadata metadata) {
        RaportProgramare entity = raportprogramareMapper.apply(row, "e");
        entity.setProgramare(programareMapper.apply(row, "programare"));
        return entity;
    }

    @Override
    public <S extends RaportProgramare> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
