package io.vantiq.ext.amqp;

import io.vantiq.ext.sdk.AbstractConnectorMain;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Map;

import static io.vantiq.ext.amqp.ConnectorConstants.*;


public class AMQPConnectorMain extends AbstractConnectorMain {

    public static void main(String[] argv) throws IOException {

        CommandLine cmd = parseCommand(argv);
        Map<String, String> connectInfo = constructConfig(cmd);
        AMQPConnector amqpConnector = new AMQPConnector(connectInfo.get(VANTIQ_SOURCE_NAME), connectInfo);
        amqpConnector.start();
    }

}
