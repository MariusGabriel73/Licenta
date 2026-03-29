package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RaportProgramareSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("ora_programata", table, columnPrefix + "_ora_programata"));
        columns.add(Column.aliased("ora_inceput_consultatie", table, columnPrefix + "_ora_inceput_consultatie"));
        columns.add(Column.aliased("durata_consultatie", table, columnPrefix + "_durata_consultatie"));

        columns.add(Column.aliased("programare_id", table, columnPrefix + "_programare_id"));
        return columns;
    }
}
