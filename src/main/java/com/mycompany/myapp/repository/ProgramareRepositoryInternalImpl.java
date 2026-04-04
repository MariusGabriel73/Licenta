package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Clinica;
import com.mycompany.myapp.domain.FisaMedicala;
import com.mycompany.myapp.domain.Medic;
import com.mycompany.myapp.domain.Pacient;
import com.mycompany.myapp.domain.Programare;
import com.mycompany.myapp.domain.RaportProgramare;
import com.mycompany.myapp.repository.rowmapper.ClinicaRowMapper;
import com.mycompany.myapp.repository.rowmapper.FisaMedicalaRowMapper;
import com.mycompany.myapp.repository.rowmapper.LocatieRowMapper;
import com.mycompany.myapp.repository.rowmapper.MedicRowMapper;
import com.mycompany.myapp.repository.rowmapper.PacientRowMapper;
import com.mycompany.myapp.repository.rowmapper.ProgramareRowMapper;
import com.mycompany.myapp.repository.rowmapper.RaportProgramareRowMapper;
import com.mycompany.myapp.repository.rowmapper.UserRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
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
    private final UserRowMapper userMapper;
    private final FisaMedicalaRowMapper fisaMedicalaMapper;
    private final RaportProgramareRowMapper raportProgramareMapper;

    private static final Table entityTable = Table.aliased("programare", EntityManager.ENTITY_ALIAS);
    private static final Table pacientTable = Table.aliased("pacient", "pacient");
    private static final Table userPacientTable = Table.aliased("jhi_user", "u_pacient");
    private static final Table medicTable = Table.aliased("medic", "medic");
    private static final Table userMedicTable = Table.aliased("jhi_user", "u_medic");
    private static final Table clinicaTable = Table.aliased("clinica", "c_joined");
    private static final Table locatieTable = Table.aliased("locatie", "l_joined");
    private static final Table fisaMedicalaTable = Table.aliased("fisa_medicala", "f_joined");
    private static final Table raportProgramareTable = Table.aliased("raport_programare", "r_joined");

    public ProgramareRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PacientRowMapper pacientMapper,
        MedicRowMapper medicMapper,
        ClinicaRowMapper clinicaMapper,
        LocatieRowMapper locatieMapper,
        ProgramareRowMapper programareMapper,
        UserRowMapper userMapper,
        FisaMedicalaRowMapper fisaMedicalaMapper,
        RaportProgramareRowMapper raportProgramareMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation<Programare, Long>(
                (RelationalPersistentEntity<Programare>) converter.getMappingContext().getRequiredPersistentEntity(Programare.class)
            ),
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
        this.fisaMedicalaMapper = fisaMedicalaMapper;
        this.raportProgramareMapper = raportProgramareMapper;
    }

    @Override
    public Flux<Programare> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Programare> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProgramareSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PacientSqlHelper.getColumns(pacientTable, "p_joined"));
        columns.addAll(MedicSqlHelper.getColumns(medicTable, "m_joined"));
        columns.addAll(UserSqlHelper.getColumns(userPacientTable, "u_pacient"));
        columns.addAll(UserSqlHelper.getColumns(userMedicTable, "u_medic"));
        columns.addAll(ClinicaSqlHelper.getColumns(clinicaTable, "c_joined"));
        columns.addAll(LocatieSqlHelper.getColumns(locatieTable, "l_joined"));
        columns.addAll(FisaMedicalaSqlHelper.getColumns(fisaMedicalaTable, "f_joined"));
        columns.addAll(RaportProgramareSqlHelper.getColumns(raportProgramareTable, "r_joined"));
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
            .equals(Column.create("id", locatieTable))
            .leftOuterJoin(fisaMedicalaTable)
            .on(Column.create("id", entityTable))
            .equals(Column.create("programare_id", fisaMedicalaTable))
            .leftOuterJoin(raportProgramareTable)
            .on(Column.create("id", entityTable))
            .equals(Column.create("programare_id", raportProgramareTable));
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
        String fromStr = fromDate.toString();
        String toStr = toDate.toString();

        Condition whereClause = Conditions.just(
            "e.medic_id = " +
            medicId +
            " AND c_joined.locatie_id = " +
            locatieId +
            " AND e.data_programare >= '" +
            fromStr +
            "'::timestamp AND e.data_programare < '" +
            toStr +
            "'::timestamp"
        );
        return createQuery(null, whereClause).all();
    }

    @Override
    public Flux<Programare> findAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate, Pageable pageable) {
        String fromStr = fromDate.toString();
        String toStr = toDate.toString();

        Condition whereClause = Conditions.just(
            "u_medic.login = '" +
            login +
            "' AND e.data_programare >= '" +
            fromStr +
            "'::timestamp AND e.data_programare < '" +
            toStr +
            "'::timestamp"
        );
        return createQuery(pageable, whereClause).all();
    }

    @Override
    public Mono<Long> countAllByMedicUserLogin(String login, java.time.Instant fromDate, java.time.Instant toDate) {
        return db
            .sql(
                "SELECT COUNT(DISTINCT e.id) FROM programare e " +
                "LEFT OUTER JOIN medic medic ON e.medic_id = medic.id " +
                "LEFT OUTER JOIN jhi_user user_medic ON medic.user_id = user_medic.id " +
                "WHERE user_medic.login = :login AND e.data_programare >= :fromDate AND e.data_programare < :toDate"
            )
            .bind("login", login)
            .bind("fromDate", fromDate)
            .bind("toDate", toDate)
            .map((row, metadata) -> row.get(0, Long.class))
            .one();
    }

    @Override
    public Flux<Programare> findAllByPacientId(Long pacientId, Pageable pageable) {
        Condition whereClause = Conditions.isEqual(entityTable.column("pacient_id"), Conditions.just(pacientId.toString()));
        return createQuery(pageable, whereClause).all();
    }

    @Override
    public Mono<Long> countAllByPacientId(Long pacientId) {
        return db
            .sql("SELECT COUNT(*) FROM programare WHERE pacient_id = :pacientId")
            .bind("pacientId", pacientId)
            .map((row, metadata) -> row.get(0, Long.class))
            .one();
    }

    @Override
    public Flux<Programare> findAllByPacientUserLogin(String login, Pageable pageable) {
        Condition whereClause = Conditions.isEqual(userPacientTable.column("login"), Conditions.just("'" + login + "'"));
        return createQuery(pageable, whereClause).all();
    }

    @Override
    public Mono<Long> countAllByPacientUserLogin(String login) {
        return db
            .sql(
                "SELECT COUNT(DISTINCT e.id) FROM programare e " +
                "LEFT OUTER JOIN pacient p ON e.pacient_id = p.id " +
                "LEFT OUTER JOIN jhi_user u_p ON p.user_id = u_p.id " +
                "WHERE u_p.login = :login"
            )
            .bind("login", login)
            .map((row, metadata) -> row.get(0, Long.class))
            .one();
    }

    private Programare process(Row row, RowMetadata metadata) {
        Programare entity = programareMapper.apply(row, "e");

        Pacient pacient = pacientMapper.apply(row, "p_joined");
        if (pacient != null && pacient.getId() != null) {
            pacient.setUser(userMapper.apply(row, "u_pacient"));
            entity.setPacient(pacient);
        }

        Medic medic = medicMapper.apply(row, "m_joined");
        if (medic != null && medic.getId() != null) {
            medic.setUser(userMapper.apply(row, "u_medic"));
            entity.setMedic(medic);
        }

        Clinica clinica = clinicaMapper.apply(row, "c_joined");
        if (clinica != null && clinica.getId() != null) {
            clinica.setLocatie(locatieMapper.apply(row, "l_joined"));
            entity.setClinica(clinica);
        }

        FisaMedicala fisaMedicala = fisaMedicalaMapper.apply(row, "f_joined");
        if (fisaMedicala != null && fisaMedicala.getId() != null) {
            entity.setFisaMedicala(fisaMedicala);
        }

        RaportProgramare raportProgramare = raportProgramareMapper.apply(row, "r_joined");
        if (raportProgramare != null && raportProgramare.getId() != null) {
            entity.setRaportProgramare(raportProgramare);
        }

        return entity;
    }

    @Override
    public <S extends Programare> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
