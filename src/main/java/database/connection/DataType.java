package database.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataType {


    private static final List<String> QUOTED_TYPES_MYSQL = Arrays.asList("CHAR", "VARCHAR", "TEXT", "BLOB", "DATE", "DATETIME", "TIME", "TIMESTAMP", "YEAR");
    private static final List<String> QUOTED_TYPES_SQLITE = Arrays.asList("CHARACTER", "VARCHAR", "NCHAR", "NVARCHAR", "TEXT", "CLOB", "BLOB", "BOOLEAN", "DATE", "DATETIME", "TIMESTAMP");
    private static final List<String> QUOTED_TYPES_ORACLE = Arrays.asList("CHAR", "NCHAR", "VARCHAR2", "NVARCHAR2", "CLOB", "NCLOB", "BLOB", "DATE", "TIMESTAMP", "INTERVAL YEAR TO MONTH", "INTERVAL DAY TO SECOND");

    public static boolean requiresQuotes(String dataType) {
        List<List<String>> listaQuotedTypes = new ArrayList<>();
        listaQuotedTypes.add(QUOTED_TYPES_ORACLE);
        listaQuotedTypes.add(QUOTED_TYPES_SQLITE);
        listaQuotedTypes.add(QUOTED_TYPES_MYSQL);
        for (List<String> list : listaQuotedTypes)
            for (String type : list)
                if (dataType.contains(type)) return true;
        return false;
    }


}

