package ru.a5x5retail.schedule;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.a5x5retail.Rmq;
import ru.a5x5retail.Settings;
import ru.a5x5retail.db.read.GetOutputMessageQuery;
import ru.a5x5retail.db.update.OutputMessageUpdateStatusQuery;
import ru.a5x5retail.model.OutputMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RabbitMqQueueSender extends AbstractSchedule {


    public RabbitMqQueueSender() {

        try {
            init();
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e);
        }
    }

    private ScheduledExecutorService ses;


    @Override
    protected void init() throws IOException {
        super.init();
        ses = Executors.newScheduledThreadPool(1);
        getChannel().basicQos(1);
        run();
    }

    private void run(){
        ses.scheduleAtFixedRate(task,5, Settings.getInstance().getServiceSettings().queueSenderTaskDelay, TimeUnit.SECONDS);
    }

    Runnable task = () -> {
        try {

            GetOutputMessageQuery q = new GetOutputMessageQuery();
            q.Execute();
            if (q.isDoError()) {
                return;
            }

            List<OutputMessage> messageList = q.getOutputMessageList();
            if (messageList == null) return;
            for (OutputMessage message : messageList) {
                log.info("File send");
                getChannel().basicPublish("defaultDirect", message.receiver, null, message.xmlData.getBytes("UTF-8"));
                OutputMessageUpdateStatusQuery query = new OutputMessageUpdateStatusQuery(message.guid, "SENT");
                query.Execute();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e);
        }
    };

    @Override
    public void stop() {
        try {
            getChannel().close();
            getChannel().getConnection().close();
            ses.shutdown();
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e);
        }
    }
}
