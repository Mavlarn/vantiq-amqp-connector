package io.vantiq.ext.amqp;

import java.util.Map;

public class AMQPConnectorConfig {

    private static final String AMQP_SERVER_HOST = "amqp_server_host";
    private static final String AMQP_SERVER_PORT = "amqp_server_port";
    private static final String AMQP_USER = "amqp_user";
    private static final String AMQP_PASSWORD = "amqp_password";
    private static final String QUEUE_NAME = "queue";

    private String amqpServer;
    private int amqpPort;
    private String amqpUser;
    private String amqpPassword;
    private String queueName;

    public static AMQPConnectorConfig fromMap(Map<String, String> sourceConfig) {
        AMQPConnectorConfig connectorConfig = new AMQPConnectorConfig();
        connectorConfig.setAmqpServer(sourceConfig.get(AMQP_SERVER_HOST));
        String amqpPortStr = sourceConfig.getOrDefault(AMQP_SERVER_PORT, "5672");
        connectorConfig.setAmqpPort(Integer.parseInt(amqpPortStr));
        connectorConfig.setAmqpUser(sourceConfig.get(AMQP_USER));
        connectorConfig.setAmqpPassword(sourceConfig.get(AMQP_PASSWORD));
        connectorConfig.setQueueName(sourceConfig.get(QUEUE_NAME));

        return connectorConfig;
    }

    public String getAmqpServer() {
        return amqpServer;
    }

    public void setAmqpServer(String amqpServer) {
        this.amqpServer = amqpServer;
    }

    public int getAmqpPort() {
        return amqpPort;
    }

    public void setAmqpPort(int amqpPort) {
        this.amqpPort = amqpPort;
    }

    public String getAmqpUser() {
        return amqpUser;
    }

    public void setAmqpUser(String amqpUser) {
        this.amqpUser = amqpUser;
    }

    public String getAmqpPassword() {
        return amqpPassword;
    }

    public void setAmqpPassword(String amqpPassword) {
        this.amqpPassword = amqpPassword;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public String toString() {
        return "AMQPConnectorConfig{" +
                "amqpServer='" + amqpServer + '\'' +
                ", amqpPort=" + amqpPort +
                ", amqpUser='" + amqpUser + '\'' +
                ", amqpPassword='" + amqpPassword + '\'' +
                ", queueName='" + queueName + '\'' +
                '}';
    }
}
