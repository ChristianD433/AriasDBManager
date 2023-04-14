package database.connection;

public class OracleDatabaseConnectionInfo extends ServerDatabaseConnectionInfo{
    private String serviceName;

    public OracleDatabaseConnectionInfo(String dbUrl, String dbUsername, String dbPassword, String serviceName) {
        super(dbUrl,dbUsername,dbPassword);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "OracleDatabaseConnectionInfo{" +
                "serviceName='" + serviceName + '\'' +
                "} " + super.toString();
    }
}
