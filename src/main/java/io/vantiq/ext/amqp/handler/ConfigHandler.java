package io.vantiq.ext.amqp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vantiq.ext.amqp.AMQPConnector;
import io.vantiq.ext.amqp.AMQPConnectorConfig;
import io.vantiq.extjsdk.ExtensionServiceMessage;
import io.vantiq.extjsdk.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.adapter.ReplyingMessageListener;
import org.springframework.util.StringUtils;

import java.util.Map;

public class ConfigHandler extends Handler<ExtensionServiceMessage> {

    static final Logger LOG = LoggerFactory.getLogger(ConfigHandler.class);

    private static final String CONFIG = "config";

    private AMQPConnector connector;
    private ObjectMapper om = new ObjectMapper();

    public ConfigHandler(AMQPConnector connector) {
        this.connector = connector;
    }

    /**
     *
     * @param message   A message to be handled
     */
    @Override
    public void handleMessage(ExtensionServiceMessage message) {
        LOG.info("Configuration for source:{}", message.getSourceName());
        Map<String, Object> configObject = (Map) message.getObject();
        Map<String, String> topicConfig;

        // Obtain entire config from the message object
        if ( !(configObject.get(CONFIG) instanceof Map)) {
            LOG.error("Configuration failed. No configuration suitable for AMQP Connector.");
            failConfig();
            return;
        }
        topicConfig = (Map) configObject.get(CONFIG);
        AMQPConnectorConfig config = AMQPConnectorConfig.fromMap(topicConfig);


        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(config.getAmqpServer(), config.getAmqpPort());
        if(StringUtils.hasText(config.getAmqpUser())) {
            connectionFactory.setUsername(config.getAmqpUser());
        }
        if(StringUtils.hasText(config.getAmqpPassword())) {
            connectionFactory.setPassword(config.getAmqpPassword());
        }

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(config.getQueueName());
        container.setMessageListener(new MessageListenerAdapter((ReplyingMessageListener) o -> {
            try {
                if (o instanceof String && ((String) o).trim().startsWith("{")) {
                    Map data = om.readValue((String)o, Map.class);
                    LOG.debug("result json string: {}", data);
                    connector.getVantiqClient().sendNotification(data);
                } else {
                    LOG.debug("result object: {}", o);
                    connector.getVantiqClient().sendNotification(o);
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
            return null;
        }));
        container.start();
        AmqpTemplate template = new RabbitTemplate(connectionFactory);
        this.connector.setAmqpTemplate(template);
        this.connector.setMqListener(container);

    }

    /**
     * Closes the source {@link AMQPConnector} and marks the configuration as completed. The source will
     * be reactivated when the source reconnects, due either to a Reconnect message (likely created by an update to the
     * configuration document) or to the WebSocket connection crashing momentarily.
     */
    private void failConfig() {
        connector.close();
    }

}
