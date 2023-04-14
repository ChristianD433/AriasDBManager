package database.connection;

public class SQLiteDatabaseConnectionInfo extends DatabaseConnectionInfo{
    private String fileName;

    public SQLiteDatabaseConnectionInfo(String dbUrl, String fileName) {
        super(dbUrl);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
