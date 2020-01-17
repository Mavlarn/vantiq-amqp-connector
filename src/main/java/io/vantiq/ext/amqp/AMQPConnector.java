package io.vantiq.ext.amqp;

import io.vantiq.ext.amqp.handler.*;
import io.vantiq.ext.sdk.ConnectorConstants;
import io.vantiq.ext.sdk.ExtensionWebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.vantiq.ext.sdk.ConnectorConstants.CONNECTOR_CONNECT_TIMEOUT;
import static io.vantiq.ext.sdk.ConnectorConstants.RECONNECT_INTERVAL;

public class AMQPConnector {

    static final Logger LOG = LoggerFactory.getLogger(AMQPConnector.class);

    ExtensionWebSocketClient vantiqClient = null;
    String sourceName;
    String vantiqUrl;
    String vantiqToken;
    String homeDir;

    AmqpTemplate amqpTemplate = null;
    SimpleMessageListenerContainer mqListener = null;

    private VantiqUtil vantiqUtil = new VantiqUtil();

    public AMQPConnector(String sourceName, Map<String, String> connectionInfo) {
        if (connectionInfo == null) {
            throw new RuntimeException("No VANTIQ connection information provided");
        }
        if (sourceName == null) {
            throw new RuntimeException("No source name provided");
        }

        this.vantiqUrl = connectionInfo.get(ConnectorConstants.VANTIQ_URL);
        this.vantiqToken = connectionInfo.get(ConnectorConstants.VANTIQ_TOKEN);
        this.homeDir = connectionInfo.get(ConnectorConstants.VANTIQ_HOME_DIR);
        this.sourceName = sourceName;
    }


    public void start() {
        vantiqClient = new ExtensionWebSocketClient(sourceName);

        vantiqClient.setConfigHandler(new ConfigHandler(this));
        vantiqClient.setReconnectHandler(new ReconnectHandler(this));
        vantiqClient.setCloseHandler(new CloseHandler(this));
        vantiqClient.setPublishHandler(new PublishHandler(this));
        vantiqClient.setQueryHandler(new QueryHandler(this));

        boolean sourcesSucceeded = false;
        while (!sourcesSucceeded) {
            vantiqClient.initiateFullConnection(vantiqUrl, vantiqToken);

            sourcesSucceeded = checkConnectionFails(vantiqClient, CONNECTOR_CONNECT_TIMEOUT);
            if (!sourcesSucceeded) {
                try {
                    Thread.sleep(RECONNECT_INTERVAL);
                } catch (InterruptedException e) {
                    LOG.error("An error occurred when trying to sleep the current thread. Error Message: ", e);
                }
            }
        }
    }

    public ExtensionWebSocketClient getVantiqClient() {
        return vantiqClient;
    }

    public VantiqUtil getVantiqUtil() {
        return this.vantiqUtil;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getVantiqUrl() {
        return vantiqUrl;
    }

    public String getVantiqToken() {
        return vantiqToken;
    }

    public SimpleMessageListenerContainer getMqListener() {
        return mqListener;
    }

    public void setMqListener(SimpleMessageListenerContainer mqListener) {
        this.mqListener = mqListener;
    }

    public AmqpTemplate getAmqpTemplate() {
        return amqpTemplate;
    }

    public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * Waits for the connection to succeed or fail, logs and exits if the connection does not succeed within
     * {@code timeout} seconds.
     *
     * @param client    The client to watch for success or failure.
     * @param timeout   The maximum number of seconds to wait before assuming failure and stopping
     * @return          true if the connection succeeded, false if it failed to connect within {@code timeout} seconds.
     */
    public boolean checkConnectionFails(ExtensionWebSocketClient client, int timeout) {
        boolean sourcesSucceeded = false;
        try {
            sourcesSucceeded = client.getSourceConnectionFuture().get(timeout, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            LOG.error("Timeout: full connection did not succeed within {} seconds: {}", timeout, e);
        }
        catch (Exception e) {
            LOG.error("Exception occurred while waiting for webSocket connection", e);
        }
        if (!sourcesSucceeded) {
            LOG.error("Failed to connect to all sources.");
            if (!client.isOpen()) {
                LOG.error("Failed to connect to server url '" + vantiqUrl + "'.");
            } else if (!client.isAuthed()) {
                LOG.error("Failed to authenticate within " + timeout + " seconds using the given authentication data.");
            } else {
                LOG.error("Failed to connect within 10 seconds");
            }
            return false;
        }
        return true;
    }
}
