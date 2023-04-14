package database.connection;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Service
public abstract class DatabaseConnectionService {

    private DatabaseConnectionInfo dbInfo;
    protected Connection connection;

    public DatabaseConnectionService(DatabaseConnectionInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public DatabaseConnectionInfo getDbInfo() {
        return dbInfo;
    }

    public void setDbInfo(DatabaseConnectionInfo dbInfo) {
        this.dbInfo = dbInfo;
    }

    public abstract void createConeccion() throws SQLException;

    public void close() throws SQLException{
        this.connection.close();
    }

    public ArrayList<String> getTables() {
        try (ResultSet rs = connection.getMetaData().getTables(connection.getCatalog(), null, null, null)) {
            addTables(rs);
        } catch (SQLException e) {
            return null;
        }
        return dbInfo.getTableNames();
    }


    public ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    protected void addTables(ResultSet rs) throws SQLException {
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            HashMap<String, String> columns = new HashMap<>();
            ResultSet rsColumns = connection.getMetaData().getColumns(connection.getCatalog(), null, tableName, null);
            while (rsColumns.next()) {
                String columnName = rsColumns.getString("COLUMN_NAME");
                String columnType = rsColumns.getString("TYPE_NAME");
                columns.put(columnName, columnType);
            }
            DatabaseTable databaseTable = new DatabaseTable();
            databaseTable.setTableName(tableName);
            databaseTable.setColumns(columns);
            getDbInfo().addTable(databaseTable);
        }
    }

    public int getTotalRows(String tableName) {
        try (ResultSet resultSet = executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    public abstract HashMap<String, ArrayList<String>> getDataFromTable(String tableName, int pageNumber, int pageSize, String orderByColumn, Boolean ascending);

    public HashMap<String, ArrayList<String>> executeSelect(String sql) {
        HashMap<String, ArrayList<String>> data = new HashMap<>();

        try (ResultSet resultSet = executeQuery(sql)) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                ArrayList<String> columnData = new ArrayList<>();
                data.put(columnName, columnData);
            }

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(columnName);
                    ArrayList<String> columnData = data.get(columnName);
                    columnData.add(columnValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return data;
    }

    public void execute(String query) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }


    public void updateTableData(String tableName, String column, String valorOriginal, Map<String, String> data) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        System.out.println(dbInfo);
        queryBuilder.append("UPDATE ").append(tableName).append(" SET ")
                .append(buildAsignation(tableName, column, data.get(column))).append(" WHERE ");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!column.equals(entry.getKey()))
                queryBuilder.append(buildAsignation(tableName, entry.getKey(), entry.getValue())).append(" AND ");
        }
        queryBuilder.append(buildAsignation(tableName, column, valorOriginal));
        System.out.println(queryBuilder);
        execute(queryBuilder.toString());
    }

    public String buildAsignation(String tableName, String columnName, String param) {
        StringBuilder asignation = new StringBuilder();
        asignation.append(columnName).append(" = ");
        if (DataType.requiresQuotes(dbInfo.getColumnType(columnName, tableName)))
            asignation.append("'").append(param).append("'");
        else asignation.append(param);
        return asignation.toString();
    }


}

