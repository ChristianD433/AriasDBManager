package database.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class SQLiteDatabaseConnectionService extends DatabaseConnectionService {
    public SQLiteDatabaseConnectionService(SQLiteDatabaseConnectionInfo dbInfo) {
        super(dbInfo);
    }

    @Override
    public void createConeccion() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        String url = this.getDbInfo().getDbUrl();
        if (!url.startsWith("jdbc:"))
            url = new StringBuilder().append("jdbc:sqlite://").append(url).toString();
        dataSource.setUrl(url);
        dataSource.setDriverClassName("org.sqlite.JDBC");
        this.connection = dataSource.getConnection();
    }

    @Override
    public void close() throws SQLException {
        super.close();
        File ficheroTemporal = new File(this.getDbInfo().getDbUrl());
        if(!ficheroTemporal.delete())
            System.out.println("No se ha podido cerrar el fichero");
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
