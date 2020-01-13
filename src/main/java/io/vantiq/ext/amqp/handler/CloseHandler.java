package io.vantiq.ext.amqp.handler;

import io.vantiq.ext.amqp.AMQPConnector;
import io.vantiq.ext.sdk.ExtensionWebSocketClient;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import static io.vantiq.ext.amqp.ConnectorConstants.CONNECTOR_CONNECT_TIMEOUT;
import static io.vantiq.ext.amqp.ConnectorConstants.RECONNECT_INTERVAL;

public class CloseHandler extends Handler<ExtensionWebSocketClient> {

    static final Logger LOG = LoggerFactory.getLogger(CloseHandler.class);

    private AMQPConnector connector;

    public CloseHandler(AMQPConnector connector) {
        this.connector = connector;
    }

    @Override
    public void handleMessage(ExtensionWebSocketClient client) {

        LOG.info("Close handler: {}", client);
        SimpleMessageListenerContainer listener = this.connector.getMqListener();
        listener.stop();
        LOG.info("Stopped listener container:{}", listener);

        // reconnect
        boolean sourcesSucceeded = false;
        while (!sourcesSucceeded) {
            client.initiateFullConnection(connector.getVantiqUrl(), connector.getVantiqToken());
            sourcesSucceeded = connector.checkConnectionFails(client, CONNECTOR_CONNECT_TIMEOUT);
            if (!sourcesSucceeded) {
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException e) {
                    LOG.error("An error occurred when trying to sleep the current thread. Error Message: ", e);
                }
            }
        }


    }
}
