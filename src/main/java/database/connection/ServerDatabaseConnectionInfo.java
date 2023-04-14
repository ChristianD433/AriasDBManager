package database.connection;

public abstract class ServerDatabaseConnectionInfo extends DatabaseConnectionInfo{
    private String dbUsername;
    private String dbPassword;

    public ServerDatabaseConnectionInfo(String dbUrl, String dbUsername, String dbPassword) {
        super(dbUrl);
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
