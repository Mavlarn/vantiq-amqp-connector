package io.vantiq.ext.amqpSource.handler;

import io.vantiq.ext.amqpSource.AMQPConnector;
import io.vantiq.ext.sdk.ExtensionWebSocketClient;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseHandler extends Handler<ExtensionWebSocketClient> {

    static final Logger LOG = LoggerFactory.getLogger(PublishHandler.class);

    private AMQPConnector extension;

    public CloseHandler(AMQPConnector extension) {
        this.extension = extension;
    }

    @Override
    public void handleMessage(ExtensionWebSocketClient client) {

        LOG.info("Close handler: {}", client);


    }
}
