package com.mycompany.myapp.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MedicSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("grad_profesional", table, columnPrefix + "_grad_profesional"));
        columns.add(Column.aliased("telefon", table, columnPrefix + "_telefon"));
        columns.add(Column.aliased("disponibil", table, columnPrefix + "_disponibil"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("sectie_id", table, columnPrefix + "_sectie_id"));
        return columns;
    }
}
