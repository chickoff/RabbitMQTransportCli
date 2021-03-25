package ru.a5x5retail.db;




import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import ru.a5x5retail.Settings;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MsSqlConnection {
    protected static final Logger log = LogManager.getLogger(MsSqlConnection.class);

    public MsSqlConnection() throws SQLException, ClassNotFoundException, SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        init();
    }

    private Connection connection;
    private String databaseName = "V_RMQ";
    private void init() throws SQLException, ClassNotFoundException, SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(getSqlConnectionString());
    }

    private String getSqlConnectionString() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        Settings settings = Settings.getInstance();
        Settings.SqlServerConnectionSettings sql = settings.getSqlServerConnectionSettings();
        return  "jdbc:sqlserver://" + sql.ip + ";databaseName=" + databaseName + ";user=" + sql.login + ";password=" + sql.password;
    }

    public Connection getConnection() {
        return connection;
    }
}
