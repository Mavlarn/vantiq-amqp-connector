package io.vantiq.ext.amqp.handler;

import io.vantiq.ext.amqp.AMQPConnector;
import io.vantiq.ext.sdk.ConnectorConstants;
import io.vantiq.ext.sdk.ExtensionServiceMessage;
import io.vantiq.ext.sdk.ExtensionWebSocketClient;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReconnectHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(ReconnectHandler.class);

    private AMQPConnector connector;

    public ReconnectHandler(AMQPConnector extension) {
        this.connector = connector;
    }

    @Override
    public void handleMessage(ExtensionServiceMessage message) {

        ExtensionWebSocketClient client = connector.getVantiqClient();
        CompletableFuture<Boolean> success = client.connectToSource();

        try {
            if ( !success.get(ConnectorConstants.CONNECTOR_CONNECT_TIMEOUT, TimeUnit.SECONDS) ) {
                if (!client.isOpen()) {
                    LOG.error("Failed to connect to server url.");
                } else if (!client.isAuthed()) {
                    LOG.error("Failed to authenticate within 10 seconds using the given authentication data.");
                } else {
                    LOG.error("Failed to connect within 10 seconds");
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOG.error("Could not reconnect to source within 10 seconds: ", e);
        }
    }
}
