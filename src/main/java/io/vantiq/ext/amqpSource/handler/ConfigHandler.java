package io.vantiq.ext.amqpSource.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import io.vantiq.ext.amqpSource.AMQPConnector;
import io.vantiq.ext.amqpSource.proto.ProtoJavaCompilerUtil;
import io.vantiq.ext.sdk.ExtensionServiceMessage;
import io.vantiq.ext.sdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.adapter.ReplyingMessageListener;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ConfigHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(AMQPConnector.class);

    private static final String CONFIG = "config";
    private static final String TOPICS_CONFIG = "topics";
    private static final String AMQP_SERVER_HOST = "amqp_server_host";
    private static final String AMQP_SERVER_PORT = "amqp_server_port";
    private static final String AMQP_USER = "amqp_user";
    private static final String AMQP_PASSWORD = "amqp_password";
    private static final String QUEUE_NAME = "queue";
    private static final String PROTO_BUF_NAME = "proto_name";
    private static final String PROTO_CLASS_NAME = "proto_class_name";


    private AMQPConnector connector;

    private ObjectMapper om = new ObjectMapper();

    public ConfigHandler(AMQPConnector connector) {
        this.connector = connector;
    }

    /**
     * topics: [
     *  {
     *      queue: "topic1", protobuf_name: "face"
     *  }
     * ]
     * @param message   A message to be handled
     */
    @Override
    public void handleMessage(ExtensionServiceMessage message) {
        LOG.warn("No configuration need for source:{}", message.getSourceName());
        Map<String, Object> configObject = (Map) message.getObject();
        Map<String, Object> config;
        List<Map<String, String>> topicsConfig;

        // Obtain entire config from the message object
        if ( !(configObject.get(CONFIG) instanceof Map)) {
            LOG.error("Configuration failed. No configuration suitable for AMQP Connector.");
            failConfig();
            return;
        }
        config = (Map) configObject.get(CONFIG);
        if ( !(config.get(TOPICS_CONFIG) instanceof List)) {
            LOG.error("Configuration failed. No configuration suitable for AMQP Connector.");
            failConfig();
            return;
        }

        topicsConfig = (List) config.get(TOPICS_CONFIG);
        for(Map<String, String> topicConfig: topicsConfig) {
            String amqpServer = topicConfig.get(AMQP_SERVER_HOST);
            String amqpPortStr = topicConfig.getOrDefault(AMQP_SERVER_PORT, "5672");
            int amqpPort = Integer.parseInt(amqpPortStr);
            String amqpUser = topicConfig.get(AMQP_USER);
            String amqpPassword = topicConfig.get(AMQP_PASSWORD);

            String queueName = topicConfig.get(QUEUE_NAME);
            String protoName = topicConfig.get(PROTO_BUF_NAME);
            String className = topicConfig.get(PROTO_CLASS_NAME);

            Class clazz = ProtoJavaCompilerUtil.compile(protoName, className, connector.getHomeDir());
            if (clazz == null) {
                continue;
            }
            try {
                final Method method = clazz.getMethod("parseFrom", byte[].class);

                JsonFormat jsonFormat = new JsonFormat();


                CachingConnectionFactory connectionFactory = new CachingConnectionFactory(amqpServer, amqpPort);
                if(StringUtils.hasText(amqpUser)) {
                    connectionFactory.setUsername(amqpUser);
                }
                if(StringUtils.hasText(amqpPassword)) {
                    connectionFactory.setPassword(amqpPassword);
                }

                SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
                container.setConnectionFactory(connectionFactory);
                container.setQueueNames(queueName);
                container.setMessageListener(new MessageListenerAdapter(new ReplyingMessageListener() {
                    @Override
                    public Object handleMessage(Object o) {
                        LOG.debug("Got amqp message:{}", o);
                        if (o instanceof byte[]) {
                            return null;
                        }

                        try {
                            Object objData = method.invoke(clazz, (byte[])o);
                            String asJson = jsonFormat.printToString((Message) objData);
                            Map data = om.readValue(asJson, Map.class);
                            LOG.debug("result json string: {}", data);
                            connector.getVantiqClient().sendNotification(data);

                        } catch (Exception e) {
                            LOG.error(e.getMessage(), e);
                        }
                        return null;
                    }
                }));
                container.start();

            } catch (NoSuchMethodException e) {
                LOG.error(e.getMessage(), e);
            }


        }




    }

    /**
     * Closes the source {@link AMQPConnector} and marks the configuration as completed. The source will
     * be reactivated when the source reconnects, due either to a Reconnect message (likely created by an update to the
     * configuration document) or to the WebSocket connection crashing momentarily.
     */
    private void failConfig() {
//        connector.close();
    }

}
