package io.vantiq.ext.amqpSource.handler;

import io.vantiq.ext.amqpSource.AMQPConnector;
import io.vantiq.ext.sdk.ExtensionServiceMessage;
import io.vantiq.ext.sdk.ExtensionWebSocketClient;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class PublishHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(PublishHandler.class);

    private AMQPConnector connector;

    public PublishHandler(AMQPConnector connector) {
        this.connector = connector;
    }

    @Override
    public void handleMessage(ExtensionServiceMessage message) {
        LOG.info("Publish called with message " + message.toString());

        String replyAddress = ExtensionServiceMessage.extractReplyAddress(message);
        ExtensionWebSocketClient client = connector.getVantiqClient();

        if ( !(message.getObject() instanceof Map) ) {
            client.sendQueryError(replyAddress, "io.vantiq.videoCapture.handler.PublishHandler",
                    "Request must be a map", null);
        }

        Map<String, String> request = (Map<String, String>) message.getObject();

        startJob(request, replyAddress);

    }

    private void startJob(Map<String, String> request, String replyAddress) {
        LOG.warn("NOT support publish for now.");
    }

}
