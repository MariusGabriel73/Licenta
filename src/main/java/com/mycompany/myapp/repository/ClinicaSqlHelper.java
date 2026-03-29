package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ClinicaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nume", table, columnPrefix + "_nume"));
        columns.add(Column.aliased("telefon", table, columnPrefix + "_telefon"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));

        columns.add(Column.aliased("locatie_id", table, columnPrefix + "_locatie_id"));
        return columns;
    }
}
