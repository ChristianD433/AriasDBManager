package database.connection;

import java.util.HashMap;

public class DatabaseTable {
    private String tableName;
    private HashMap<String, String> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnType(String columnName){
        if(!columns.containsKey(columnName))
            return null;
        return columns.get(columnName);
    }

    public HashMap<String, String> getColumns() {
        return columns;
    }

    public void setColumns(HashMap<String, String> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "DatabaseTable{" +
                "tableName='" + tableName + '\'' +
                ", columns=" + columns +
                '}';
    }
}
