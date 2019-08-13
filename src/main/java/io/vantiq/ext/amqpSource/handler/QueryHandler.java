package io.vantiq.ext.amqpSource.handler;

import io.vantiq.ext.amqpSource.AMQPConnector;
import io.vantiq.ext.sdk.ExtensionServiceMessage;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class QueryHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(PublishHandler.class);

    private AMQPConnector extension;

    public QueryHandler(AMQPConnector extension) {
        this.extension = extension;
    }

    @Override
    public void handleMessage(ExtensionServiceMessage msg) {

        LOG.info("Query handler:  Got query: {}", msg);
        String replyAddress = ExtensionServiceMessage.extractReplyAddress(msg);

        Object maybeMap = msg.getObject();
        if (!(maybeMap instanceof Map)) {
            LOG.error("Query Failed: Message format error -- 'object' was a {}, should be Map.  Overall message: {}",
                    maybeMap.getClass().getName(), msg);
            Object[] parms = new Object[] {maybeMap.getClass().getName()};
            extension.getVantiqClient().sendQueryError(replyAddress, this.getClass().getName() + ".QueryFormatError",
                    "Query message was malformed.  'object' should be a Map, it was {}.", parms);
        } else {
            // AMQP doesn't support query

                /*Map responseMap = client.performQuery((Map) maybeMap);
                if (responseMap != null)
                {
                    vantiqClient.sendQueryResponse(200, replyAddress, responseMap);
                }
                else
                {
                    Object[] parms = new Object[] {msg};

                    vantiqClient.sendQueryError(replyAddress, this.getClass().getName() + ".QueryFormatError",
                            "Query message was malformed. {} ", parms);
                }
                */
        }
    }
}
