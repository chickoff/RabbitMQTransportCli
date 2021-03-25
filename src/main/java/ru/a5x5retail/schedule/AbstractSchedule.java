package ru.a5x5retail.schedule;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.a5x5retail.RmqMainClass;
import ru.a5x5retail.Settings;

import java.io.IOException;

public abstract class AbstractSchedule {
    protected static final Logger log = LogManager.getLogger(AbstractSchedule.class);
    public abstract void stop();


    public Connection getConnection() {
        return connection;
    }

    public Channel getChannel() {
        return channel;
    }

    private Connection connection;
    private Channel channel;

    protected void init() throws IOException {
        initConnection ();
        initChanel();
    }

    private void initConnection () {
        Settings.RabbitMQConnectionSettings rabbitSettings = Settings.getInstance().getRabbitMQConnectionSettings();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitSettings.ip);
        factory.setUsername(rabbitSettings.login);
        factory.setPassword(rabbitSettings.password);
        factory.setNetworkRecoveryInterval(15000);
        factory.setConnectionTimeout(5000);

        boolean connectionIsSuccess = false;
        Connection connection = null;
        while (!connectionIsSuccess && !RmqMainClass.isStoppingService) {
            try {
                log.trace("connection ");
                connection = factory.newConnection();
                log.trace("connection NEW");
                connectionIsSuccess = true;
            } catch(Throwable ex) {
                log.error(ex);
                ex.printStackTrace();

                try {
                    Thread.sleep(15000);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        this.connection = connection;
        this.connection.addShutdownListener(cause -> {
            log.error(cause.getMessage());
        });

        this.connection.addBlockedListener(new BlockedListener() {
            @Override
            public void handleBlocked(String reason) throws IOException {
                log.error(reason);
            }

            @Override
            public void handleUnblocked() throws IOException {

            }
        });

        this.connection = connection;
    }

    private void initChanel() throws IOException {

        Channel channel = connection.createChannel();
        channel.basicQos(1);
        this.channel = channel;
    }
}
