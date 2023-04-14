package database.connection;


import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public abstract class DatabaseConnectionInfo {
    private String dbUrl;

    private ArrayList<DatabaseTable> listaTablas = new ArrayList<>();

    public void addTable(DatabaseTable databaseTable) {
        listaTablas.add(databaseTable);
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public DatabaseConnectionInfo(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getColumnType(String columnName, String tableName) {
        for (DatabaseTable table : listaTablas)
            if (table.getTableName().equals(tableName)) return table.getColumnType(columnName);
        return null;
    }

    @Override
    public String toString() {
        return "DatabaseConnectionInfo{" + ", dbUrl='" + dbUrl + '\'' + ", listaTablas=" + listaTablas + '}';
    }

    public ArrayList<String> getTableNames() {
        ArrayList<String> listNames = new ArrayList<>();
        for (DatabaseTable databaseTable : listaTablas)
            listNames.add(databaseTable.getTableName());
        return listNames;
    }

    public void addToUrl(String databaseName) {
        if (databaseName != null && !databaseName.isEmpty()) this.dbUrl += "/" + databaseName;
    }

}
