package io.vantiq.ext.amqp;

import io.vantiq.ext.amqp.handler.*;
import io.vantiq.extjsdk.VantiqConnector;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;


public class AMQPConnector extends VantiqConnector {

    AmqpTemplate amqpTemplate = null;
    SimpleMessageListenerContainer mqListener = null;

    @Override
    public void init() {
        vantiqClient.setConfigHandler(new ConfigHandler(this));
        vantiqClient.setReconnectHandler(new ReconnectHandler(this));
        vantiqClient.setCloseHandler(new CloseHandler(this));
        vantiqClient.setPublishHandler(new PublishHandler(this));
        vantiqClient.setQueryHandler(new QueryHandler(this));
    }

    public SimpleMessageListenerContainer getMqListener() {
        return mqListener;
    }

    public void setMqListener(SimpleMessageListenerContainer mqListener) {
        this.mqListener = mqListener;
    }

    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

}
