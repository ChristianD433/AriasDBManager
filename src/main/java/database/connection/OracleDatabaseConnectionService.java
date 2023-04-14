package database.connection;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;


public class OracleDatabaseConnectionService extends DatabaseConnectionService {
    public OracleDatabaseConnectionService(OracleDatabaseConnectionInfo dbInfo) {
        super(dbInfo);
    }

    @Override
    public void createConeccion() throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        String url = this.getDbInfo().getDbUrl();
        OracleDatabaseConnectionInfo oracleDatabaseConnectionInfo = (OracleDatabaseConnectionInfo) this.getDbInfo();
        if (!url.startsWith("jdbc:"))
            url = new StringBuilder().append("jdbc:oracle:thin:@//").append(url)
                    .append("/").append(oracleDatabaseConnectionInfo.getServiceName()).toString();
        System.out.println(url);
        dataSource.setUrl(url);
        dataSource.setUsername(oracleDatabaseConnectionInfo.getDbUsername());
        dataSource.setPassword(oracleDatabaseConnectionInfo.getDbPassword());
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        this.connection = dataSource.getConnection();
    }
    @Override
    public ArrayList<String> getTables() {
        try {
            String schemaPattern = connection.getMetaData().getUserName().toUpperCase();
            String[] types = {"TABLE"};
            ResultSet rs = connection.getMetaData().getTables(connection.getCatalog(), schemaPattern, null, types);
            addTables(rs);
            return getDbInfo().getTableNames();
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public HashMap<String, ArrayList<String>> getDataFromTable(String tableName, int pageNumber, int pageSize, String orderByColumn, Boolean ascending) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName);

        if (orderByColumn != null && ascending != null) {
            String orderDirection = ascending ? "ASC" : "DESC";
            sql.append(" ORDER BY ").append(orderByColumn).append(" ").append(orderDirection);
        }

        int offset = (pageNumber - 1) * pageSize; // Se calcula el offset en función del número de página
        sql.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(pageSize).append(" ROWS ONLY");

        return executeSelect(sql.toString());
    }

}
