package io.vantiq.ext.amqp.handler;

import io.vantiq.ext.amqp.AMQPConnector;
import io.vantiq.extjsdk.ExtensionServiceMessage;
import io.vantiq.extjsdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(QueryHandler.class);

    private AMQPConnector extension;

    public QueryHandler(AMQPConnector extension) {
        this.extension = extension;
    }

    @Override
    public void handleMessage(ExtensionServiceMessage msg) {

        LOG.warn("AMQP not support query");
//        String replyAddress = ExtensionServiceMessage.extractReplyAddress(msg);
//
//        Object maybeMap = msg.getObject();
//        if (!(maybeMap instanceof Map)) {
//            LOG.error("Query Failed: Message format error -- 'object' was a {}, should be Map.  Overall message: {}",
//                    maybeMap.getClass().getName(), msg);
//            Object[] parms = new Object[] {maybeMap.getClass().getName()};
//            extension.getVantiqClient().sendQueryError(replyAddress, this.getClass().getName() + ".QueryFormatError",
//                    "Query message was malformed.  'object' should be a Map, it was {}.", parms);
//        } else {
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

        } */
    }
}
