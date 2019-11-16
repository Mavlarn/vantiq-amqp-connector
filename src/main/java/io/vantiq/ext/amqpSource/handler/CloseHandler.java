package io.vantiq.ext.amqpSource.handler;

import io.vantiq.ext.amqpSource.AMQPConnector;
import io.vantiq.ext.sdk.ExtensionWebSocketClient;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.Map;

public class CloseHandler extends Handler<ExtensionWebSocketClient> {

    static final Logger LOG = LoggerFactory.getLogger(PublishHandler.class);

    private AMQPConnector connector;

    public CloseHandler(AMQPConnector connector) {
        this.connector = connector;
    }

    @Override
    public void handleMessage(ExtensionWebSocketClient client) {

        LOG.info("Close handler: {}", client);

        Map<String, SimpleMessageListenerContainer> listeners = connector.getMQListeners();
        for (SimpleMessageListenerContainer listener: listeners.values()) {
            listener.stop();
            LOG.info("Stopped listener container:{}", listener);
        }


    }
}
