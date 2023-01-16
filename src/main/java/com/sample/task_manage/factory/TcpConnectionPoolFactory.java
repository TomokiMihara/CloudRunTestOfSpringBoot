package com.sample.task_manage.factory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class TcpConnectionPoolFactory extends ConnectionPoolFactory {
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASS = System.getenv("DB_PASS");
    private static final String DB_NAME = System.getenv("DB_NAME");

    private static final String INSTANCE_HOST = System.getenv("INSTANCE_HOST");
    private static final String DB_PORT = System.getenv("DB_PORT");

    // [END cloud_sql_mysql_servlet_connect_tcp]
    private static final String TRUST_CERT_KEYSTORE_PATH = System.getenv(
            "TRUST_CERT_KEYSTORE_PATH");
    private static final String TRUST_CERT_KEYSTORE_PASSWD = System.getenv(
            "TRUST_CERT_KEYSTORE_PASSWD");
    private static final String CLIENT_CERT_KEYSTORE_PATH = System.getenv(
            "CLIENT_CERT_KEYSTORE_PATH");
    private static final String CLIENT_CERT_KEYSTORE_PASSWD = System.getenv(
            "CLIENT_CERT_KEYSTORE_PASSWD");
    // [START cloud_sql_mysql_servlet_connect_tcp]

    public static DataSource createConnectionPool() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", INSTANCE_HOST, DB_PORT, DB_NAME));
        config.setUsername(DB_USER);
        config.setPassword(DB_PASS);

        if (CLIENT_CERT_KEYSTORE_PATH != null && TRUST_CERT_KEYSTORE_PATH != null) {
            config.addDataSourceProperty("trustCertificateKeyStoreUrl",
                    String.format("file:%s", TRUST_CERT_KEYSTORE_PATH));
            config.addDataSourceProperty("trustCertificateKeyStorePassword", TRUST_CERT_KEYSTORE_PASSWD);
            config.addDataSourceProperty("clientCertificateKeyStoreUrl",
                    String.format("file:%s", CLIENT_CERT_KEYSTORE_PATH));
            config.addDataSourceProperty("clientCertificateKeyStorePassword",
                    CLIENT_CERT_KEYSTORE_PASSWD);
        }

        configureConnectionPool(config);

        return new HikariDataSource(config);
    }
}
