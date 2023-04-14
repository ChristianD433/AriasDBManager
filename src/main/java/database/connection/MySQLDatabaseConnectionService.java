package database.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MySQLDatabaseConnectionService extends DatabaseConnectionService {
    public MySQLDatabaseConnectionService(MySQLDatabaseConnectionInfo dbInfo) {
        super(dbInfo);
    }

    public List<String> getDatabases() {
        DatabaseMetaData metaData = null;
        try {
            metaData = this.connection.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (ResultSet resultSet = metaData.getCatalogs()){
            List<String> databases = new ArrayList<>();
            while (resultSet.next()) {
                databases.add(resultSet.getString("TABLE_CAT"));
            }
            return databases;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void enterDataBase(String databaseName) throws SQLException {
        this.getDbInfo().addToUrl(databaseName);
        this.createConeccion();
    }

    @Override
    public void createConeccion() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        String url = this.getDbInfo().getDbUrl();
        if (!url.startsWith("jdbc:"))
            url = new StringBuilder().append("jdbc:mysql://").append(url).toString();
        dataSource.setUrl(url);
        MySQLDatabaseConnectionInfo mySQLDatabaseConnectionInfo = (MySQLDatabaseConnectionInfo) this.getDbInfo();
        dataSource.setUsername(mySQLDatabaseConnectionInfo.getDbUsername());
        dataSource.setPassword(mySQLDatabaseConnectionInfo.getDbPassword());
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        this.connection = dataSource.getConnection();
    }

    @Override
    public HashMap<String, ArrayList<String>> getDataFromTable(String tableName, int pageNumber, int pageSize, String orderByColumn, Boolean ascending) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName);
        if (orderByColumn != null && ascending != null) {
            String orderDirection = ascending ? "ASC" : "DESC";
            sql.append(" ORDER BY ").append(orderByColumn).append(" ").append(orderDirection);
        }
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append((pageNumber - 1) * pageSize);
        System.out.println(sql);
        return executeSelect(sql.toString());
    }




}
