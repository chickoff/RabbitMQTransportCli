package ru.a5x5retail;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Rmq {

    protected static final Logger log = LogManager.getLogger(Rmq.class);

    public static Connection getQueueServerConnection() {
        /*Settings.RabbitMQConnectionSettings rabbitSettings = Settings.getInstance().getRabbitMQConnectionSettings();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitSettings.ip);
        factory.setUsername(rabbitSettings.login);
        factory.setPassword(rabbitSettings.password);

        boolean connectionIsSuccess = false;
        Connection connection = null;
        while (!connectionIsSuccess) {
            try {
                connection = factory.newConnection();
                connectionIsSuccess = true;
            } catch(Throwable ex) {
                log.error(ex);
                ex.printStackTrace();

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/

        return null;
    }

    public  static Channel getChanel() throws IOException {
        Connection connection = getQueueServerConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);
        return channel;
    }
}
