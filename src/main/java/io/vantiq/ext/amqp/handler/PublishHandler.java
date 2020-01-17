package io.vantiq.ext.amqp.handler;

import io.vantiq.ext.amqp.AMQPConnector;
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
        LOG.debug("Publish with message " + message.toString());

        String replyAddress = ExtensionServiceMessage.extractReplyAddress(message);
        ExtensionWebSocketClient client = connector.getVantiqClient();

        if ( !(message.getObject() instanceof Map) ) {
            client.sendQueryError(replyAddress, "io.vantiq.videoCapture.handler.PublishHandler",
                    "Request must be a map", null);
        }

        Map<String, ?> request = (Map<String, ?>) message.getObject();

        publish(request);

    }

    private void publish(Map<String, ?> request) {

        // Gather query results, or send a query error if an exception is caught
        try {
            String topic = (String)request.get("topic");
            Object message = request.get("message");
            this.connector.getAmqpTemplate().convertAndSend(topic, message);
            LOG.trace("Sent message: ", message);
        } catch (Exception e) {
            LOG.error("An unexpected error occurred when executing publish.", e);
            LOG.error("Request was: {}", request);
        }
    }

}
