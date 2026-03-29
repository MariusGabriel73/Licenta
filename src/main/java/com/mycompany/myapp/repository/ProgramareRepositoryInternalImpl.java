package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.domain.Pacient;
import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.repository.rowmapper.ClinicaRowMapper;
import com.mycompany.myapp.repository.rowmapper.MedicRowMapper;
import com.mycompany.myapp.repository.rowmapper.PacientRowMapper;
import com.mycompany.myapp.repository.rowmapper.LocatieRowMapper;
import com.mycompany.myapp.repository.rowmapper.ProgramareRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Programare entity.
 */
@SuppressWarnings("unused")
class ProgramareRepositoryInternalImpl extends SimpleR2dbcRepository<Programare, Long> implements ProgramareRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PacientRowMapper pacientMapper;
    private final MedicRowMapper medicMapper;
    private final ClinicaRowMapper clinicaMapper;
    private final LocatieRowMapper locatieMapper;
    private final ProgramareRowMapper programareMapper;

    private static final Table entityTable = Table.aliased("programare", EntityManager.ENTITY_ALIAS);
    private static final Table pacientTable = Table.aliased("pacient", "pacient");
    private static final Table userPacientTable = Table.aliased("jhi_user", "user_pacient");
    private static final Table medicTable = Table.aliased("medic", "medic");
    private static final Table userMedicTable = Table.aliased("jhi_user", "user_medic");
    private static final Table clinicaTable = Table.aliased("clinica", "clinica");
    private static final Table locatieTable = Table.aliased("locatie", "locatie");

    private final UserRowMapper userMapper;

    public ProgramareRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PacientRowMapper pacientMapper,
        MedicRowMapper medicMapper,
        ClinicaRowMapper clinicaMapper,
        LocatieRowMapper locatieMapper,
        ProgramareRowMapper programareMapper,
        UserRowMapper userMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Programare.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pacientMapper = pacientMapper;
        this.medicMapper = medicMapper;
        this.clinicaMapper = clinicaMapper;
        this.locatieMapper = locatieMapper;
        this.programareMapper = programareMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Flux<Programare> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Programare> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProgramareSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PacientSqlHelper.getColumns(pacientTable, "pacient"));
        columns.addAll(UserSqlHelper.getColumns(userPacientTable, "user_pacient"));
        columns.addAll(MedicSqlHelper.getColumns(medicTable, "medic"));
        columns.addAll(UserSqlHelper.getColumns(userMedicTable, "user_medic"));
        columns.addAll(ClinicaSqlHelper.getColumns(clinicaTable, "clinica"));
        columns.addAll(LocatieSqlHelper.getColumns(locatieTable, "locatie"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pacientTable)
            .on(Column.create("pacient_id", entityTable))
            .equals(Column.create("id", pacientTable))
            .leftOuterJoin(userPacientTable)
            .on(Column.create("user_id", pacientTable))
            .equals(Column.create("id", userPacientTable))
            .leftOuterJoin(medicTable)
            .on(Column.create("medic_id", entityTable))
            .equals(Column.create("id", medicTable))
            .leftOuterJoin(userMedicTable)
            .on(Column.create("user_id", medicTable))
            .equals(Column.create("id", userMedicTable))
            .leftOuterJoin(clinicaTable)
            .on(Column.create("clinica_id", entityTable))
            .equals(Column.create("id", clinicaTable))
            .leftOuterJoin(locatieTable)
            .on(Column.create("locatie_id", clinicaTable))
            .equals(Column.create("id", locatieTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Programare.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Programare> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Programare> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Flux<Programare> findByMedicIdAndLocatieIdAndDataProgramareBetween(
        Long medicId,
        Long locatieId,
        java.time.Instant fromDate,
        java.time.Instant toDate
    ) {
        Condition whereClause = Conditions.just(
            "e.medic_id = " + medicId + " AND clinica.locatie_id = " + locatieId + " AND e.data_programare >= '" + fromDate.toString() + "' AND e.data_programare < '" + toDate.toString() + "'"
        );
        return createQuery(null, whereClause).all();
    }

    @Override
    public Flux<Programare> findAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate, Pageable pageable) {
        Condition whereClause = Conditions.just("user_medic.login = '" + login + "' AND e.data_programare >= '" + fromDate.toString() + "' AND e.data_programare < '" + toDate.toString() + "'");
        return createQuery(pageable, whereClause).all();
    }

    @Override
    public Mono<Long> countAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate) {
        return db.sql("SELECT COUNT(DISTINCT e.id) FROM programare e " +
            "LEFT OUTER JOIN medic medic ON e.medic_id = medic.id " +
            "LEFT OUTER JOIN jhi_user user_medic ON medic.user_id = user_medic.id " +
            "WHERE user_medic.login = :login AND e.data_programare >= :fromDate AND e.data_programare < :toDate")
            .bind("login", login)
            .bind("fromDate", fromDate)
            .bind("toDate", toDate)
            .map((row, metadata) -> row.get(0, Long.class))
            .one();
    }


    private Programare process(Row row, RowMetadata metadata) {
        Programare entity = programareMapper.apply(row, "e");

        Pacient pacient = pacientMapper.apply(row, "pacient");
        if (pacient != null) {
            pacient.setUser(userMapper.apply(row, "user_pacient"));
        }
        entity.setPacient(pacient);

        Medic medic = medicMapper.apply(row, "medic");
        if (medic != null) {
            medic.setUser(userMapper.apply(row, "user_medic"));
        }
        entity.setMedic(medic);

        Clinica clinica = clinicaMapper.apply(row, "clinica");
        if (clinica != null) {
            clinica.setLocatie(locatieMapper.apply(row, "locatie"));
        }
        entity.setClinica(clinica);
        return entity;
    }

    @Override
    public <S extends Programare> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
