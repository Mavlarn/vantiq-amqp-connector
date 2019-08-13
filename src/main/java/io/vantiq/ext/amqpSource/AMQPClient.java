package io.vantiq.ext.amqpSource;

import io.vantiq.ext.sdk.ExtensionWebSocketClient;

import java.util.Map;

public class AMQPClient {

    ExtensionWebSocketClient vantiqClient = null;
    Map configurationDoc = null;

    public void connect(ExtensionWebSocketClient client, Map config)
    {
        this.vantiqClient = client;
        this.configurationDoc = config;
    }

    public void performQuery(Map parameters)
    {

    }

}
