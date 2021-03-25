package ru.a5x5retail.schedule;

import com.rabbitmq.client.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.xml.sax.SAXException;
import ru.a5x5retail.Rmq;
import ru.a5x5retail.RmqMainClass;
import ru.a5x5retail.Settings;
import ru.a5x5retail.db.create.InsertInputMessageQuery;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

public class RabbitMqQueueReceive extends AbstractSchedule {
    private ScheduledExecutorService ses;



    public RabbitMqQueueReceive() throws IOException {
        init();
    }

    @Override
    protected void init() throws IOException {
        super.init();
        Settings.RabbitMQConnectionSettings rabbitSettings = Settings.getInstance().getRabbitMQConnectionSettings();
        getChannel().basicQos(1);
        getChannel().queueDeclare(rabbitSettings.queueName, true, false, false, null);
        getChannel().queueBind(rabbitSettings.queueName, "defaultDirect", rabbitSettings.queueName);

        log.info("---".concat("Allowed exchanges:"));

        for (Settings.AllowExchanges allowExchange:
        Settings.getAllowExchangesList()) {
            try {
                getChannel().queueBind(rabbitSettings.queueName, allowExchange.exchangeName, allowExchange.routingKey);
                log.info("---".concat(allowExchange.exchangeName));
            } catch (Throwable e) {
                log.error(e);
            }
        }

        log.trace(rabbitSettings.queueName);
        String s = getChannel().basicConsume(rabbitSettings.queueName, false, deliverCallback, cancelCallback);
        log.error(s);
    }


    DeliverCallback deliverCallback = (consumerTag, delivery) -> {

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(delivery.getBody());
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

            boolean isRead = true;

            String sender = null, partType = null, xmlData = null;

            while (isRead) {
                if (reader.hasNext()) {
                    if (reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                        if (reader.getLocalName().equalsIgnoreCase("Root")) {
                            sender = reader.getAttributeValue("", "Sender");
                            partType = reader.getAttributeValue("", "PartType");
                            isRead = false;
                        }
                    } else {
                        reader.next();
                    }
                }
            }

            if (sender == null || partType == null) {
                getChannel().basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                return;
            }

            if (sender.equals(Settings.getInstance().getRabbitMQConnectionSettings().queueName)) {
                getChannel().basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                return;
            }

            xmlData = new String(delivery.getBody(),"UTF-8");

            InsertInputMessageQuery q = new InsertInputMessageQuery(sender,partType,xmlData);
            q.Execute();
            if (!q.isDoError()) {
                getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
            else
            {
                getChannel().basicNack(delivery.getEnvelope().getDeliveryTag(),false,true);
                log.error(q.getException());
            }

        } catch (Throwable e) {
            getChannel().basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            log.error(e);
            e.printStackTrace();
        }
    };

    CancelCallback cancelCallback = consumerTag -> {}
    ;

    @Override
    public void stop() {

        log.trace("Stop RabbitMqQueueReceive");
        try {

            getChannel().close();
            log.trace("Channel close RabbitMqQueueReceive");

            getChannel().getConnection().close();
            log.trace("Connection close RabbitMqQueueReceive");
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e);
        }
    }
}
