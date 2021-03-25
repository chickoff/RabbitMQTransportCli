package ru.a5x5retail;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Settings {

    public Settings(String rootAppPath) {
        this.rootAppPath = rootAppPath;
    }

    private String rootAppPath;
    private static Settings settings;

    public static void createInstance(String rootAppPath) {
        settings = new Settings(rootAppPath);
    }

    public static Settings getInstance() {
        return settings;
    }



    private ServiceSettings serviceSettings;

    public ServiceSettings getServiceSettings() {
        return serviceSettings;
    }

    public void setServiceSettings(ServiceSettings serviceSettings) {
        this.serviceSettings = serviceSettings;
    }




    private RabbitMQConnectionSettings rabbitMQConnectionSettings;
    public RabbitMQConnectionSettings getRabbitMQConnectionSettings() {
        return rabbitMQConnectionSettings;
    }
    public void setRabbitMQConnectionSettings(RabbitMQConnectionSettings rabbitMQConnectionSettings) {
        this.rabbitMQConnectionSettings = rabbitMQConnectionSettings;
    }


    public SqlServerConnectionSettings getSqlServerConnectionSettings() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        Node value = readValue("Settings/SqlServer");
        NamedNodeMap attr = value.getAttributes();
        SqlServerConnectionSettings sqlServerConnectionSettings = new SqlServerConnectionSettings();
        sqlServerConnectionSettings.ip = getNodeValue(attr,"ip");
        sqlServerConnectionSettings.login = getNodeValue(attr,"login");
        sqlServerConnectionSettings.password = getNodeValue(attr,"password");
        return sqlServerConnectionSettings;
    }

    private String getNodeValue(NamedNodeMap attrMap, String name) {
        for (int i = 0; i < attrMap.getLength(); i++) {
            Node n = attrMap.item(i);
            if (n.getNodeName().equals(name)) {
                return n.getNodeValue();
            }
        }
        return null;
    }

    private Node readValue(String xPath) throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        Node value = null;
        value =readValueImpl(xPath);
        return value;
    }

    private Node readValueImpl(String xPath) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {

        Node value = null;

        Path path = Paths.get(rootAppPath,"Settings","settings.xml");
        File file = path.toFile();
        if (!file.exists()){
            createFile(file);
        }

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.parse(file);

        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        XPathExpression expr = xpath.compile(xPath);
        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

        if (nodes.getLength() > 0 ) {
            value = nodes.item(0);
        }

        return value;
    }

    private void createFile(File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        file.createNewFile();
        StringBuilder sb = new StringBuilder();
        sb  .append("<Settings>")
            .append("<SqlServer ip=\"127.0.0.1\" login=\"sa\" password=\"mssql\"/>")
            .append("</Settings>");
        FileWriter fw = new FileWriter(file);
        fw.write(sb.toString());
        fw.close();
    }

    public class SqlServerConnectionSettings {
        public String ip,login,password;
    }

    public static class RabbitMQConnectionSettings {
        public String ip,login,password,queueName;
    }

    public static class ServiceSettings {
        public boolean isOffice;
        public int queueSenderTaskDelay;
        public boolean isStore;
    }

    private static List<AllowExchanges> allowExchangesList;
    public static List<AllowExchanges> getAllowExchangesList() {
        if (allowExchangesList == null){
            allowExchangesList = new ArrayList<>();
        }
        return allowExchangesList;
    }

    public static void setAllowExchangesList(List<AllowExchanges> allowExchangesList) {
        Settings.allowExchangesList = allowExchangesList;
    }


    public static class AllowExchanges {
        public String exchangeName;
        public String routingKey;
        public boolean isEnable;
    }
}
