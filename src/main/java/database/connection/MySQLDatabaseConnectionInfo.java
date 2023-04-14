package database.connection;

public class MySQLDatabaseConnectionInfo extends ServerDatabaseConnectionInfo{

    public MySQLDatabaseConnectionInfo(String dbUrl, String dbUsername, String dbPassword) {
        super(dbUrl, dbUsername, dbPassword);
    }

    @Override
    public String toString() {
        return "MySQLDatabaseConnectionInfo{} " + super.toString();
    }
}
