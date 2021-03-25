package ru.a5x5retail;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import ru.a5x5retail.db.read.GetAllowExchangesQuery;
import ru.a5x5retail.db.read.GetSettingsQuery;
import ru.a5x5retail.schedule.AbstractSchedule;
import ru.a5x5retail.schedule.RabbitMqQueueReceive;
import ru.a5x5retail.schedule.RabbitMqQueueSender;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class RmqMainClass {




    private static String version = "0.2.2014";
    public static boolean isStoppingService = false;

    protected static final Logger log = LogManager.getLogger(RmqMainClass.class);

    public static void main(String[] args) {

        String s = "";

        for (int i = 0; i < 1000000; i++)
        {
            s += "qqq";
        }


        BasicConfigurator.configure();

        log.info("Logger started...");
        log.info("Service version: ".concat(version) );

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        log.info("JVM Arguments: ");


        for (String arg:arguments) {
            log.info("---".concat(arg));
        }

        List<GarbageCollectorMXBean> gcList = ManagementFactory.getGarbageCollectorMXBeans();

        for (GarbageCollectorMXBean garbageCollectorMXBean : gcList) {
            log.info("---".concat(garbageCollectorMXBean.getName()));
        }

        Settings.createInstance(MainEnvironment.getAppPath());
        log.trace("Settings");
        getServerSettings();
        log.trace("Server Settings");
        getAllowExchangeList();

        try {

            schedules.add(new RabbitMqQueueReceive());
            log.trace("RabbitMqQueueReceive");
            schedules.add(new RabbitMqQueueSender());
            log.trace("RabbitMqQueueSender");

        } catch (Throwable e) {
            log.error(e);
            e.printStackTrace();
        }
    }

    private static HashSet<AbstractSchedule> schedules = new HashSet<>();


    public static void stop(String[] args) {
        log.trace("Stop");
        isStoppingService = true;

        for (AbstractSchedule schedule : schedules) {
            schedule.stop();
        }
    }


    private static void getServerSettings() {

        Settings.RabbitMQConnectionSettings rabbitMQConnectionSettings = new Settings.RabbitMQConnectionSettings();

        boolean isDbSettingsComplete = false;
        while ( !isDbSettingsComplete && !RmqMainClass.isStoppingService ) {

            boolean isIp,isLogin,isPwd,isQueueName,
                    isOfficeSetting,isQueueSenderTaskDelay,isStoreSetting;
            isIp = isLogin = isPwd = isQueueName = isOfficeSetting = isQueueSenderTaskDelay = isStoreSetting = false;

            GetSettingsQuery settingsQuery;

            settingsQuery = new GetSettingsQuery("RmqServer");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isIp = true;
                rabbitMQConnectionSettings.ip = settingsQuery.getResult();
            }

            settingsQuery = new GetSettingsQuery("RmqUser");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isLogin = true;
                rabbitMQConnectionSettings.login = settingsQuery.getResult();
            }

            settingsQuery = new GetSettingsQuery("RmqPwd");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isPwd = true;
                rabbitMQConnectionSettings.password = settingsQuery.getResult();
            }

            settingsQuery = new GetSettingsQuery("RmqQueueName");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isQueueName = true;
                rabbitMQConnectionSettings.queueName = settingsQuery.getResult();
            }

            Settings.ServiceSettings serviceSettings = new Settings.ServiceSettings();

            settingsQuery = new GetSettingsQuery("isOffice");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isOfficeSetting = true;
                serviceSettings.isOffice = !"0".equals(settingsQuery.getResult());
            }

            settingsQuery = new GetSettingsQuery("QueueSenderTaskDelay");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isQueueSenderTaskDelay = true;
                serviceSettings.queueSenderTaskDelay = Integer.parseInt(settingsQuery.getResult());
            }

            settingsQuery = new GetSettingsQuery("isStore");
            settingsQuery.Execute();
            if (!settingsQuery.isDoError()) {
                isOfficeSetting = true;
                serviceSettings.isStore = !"0".equals(settingsQuery.getResult());
            }



            if (isIp & isLogin & isPwd & isQueueName & isOfficeSetting & isQueueSenderTaskDelay & isOfficeSetting) {
                Settings.getInstance().setRabbitMQConnectionSettings(rabbitMQConnectionSettings);
                Settings.getInstance().setServiceSettings(serviceSettings);
                isDbSettingsComplete = true;
            } else {
                try {
                    Thread.sleep(15000);
                } catch (Throwable e) {
                    log.error(e);
                    e.printStackTrace();
                }
            }
        }
    }


    private static void getAllowExchangeList() {
        GetAllowExchangesQuery q = new GetAllowExchangesQuery();
        q.Execute();
        if (!q.isDoError()) {
            Settings.setAllowExchangesList(q.getAllowExchangesList());
        }
    }

    private static void initQueueServer() {
        try {
            Settings.RabbitMQConnectionSettings rabbitSettings = Settings.getInstance().getRabbitMQConnectionSettings();
            Channel channel = Rmq.getChanel();
            channel.basicQos(1);
            channel.queueDeclare(rabbitSettings.queueName, true, false, false, null);
            channel.queueBind(rabbitSettings.queueName, "defaultDirect", rabbitSettings.queueName);

            if (!Settings.getInstance().getServiceSettings().isOffice) {
                channel.queueBind(rabbitSettings.queueName, "shopsFanout", "");
            }

            channel.close();
            channel.getConnection().close();

        } catch (Throwable e) {

        }
    }
}
