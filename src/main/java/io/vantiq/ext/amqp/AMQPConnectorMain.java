package io.vantiq.ext.amqp;

import io.vantiq.ext.sdk.AbstractConnectorMain;
import org.apache.commons.cli.CommandLine;

import java.util.Map;

import static io.vantiq.ext.sdk.ConnectorConstants.VANTIQ_SOURCE_NAME;


public class AMQPConnectorMain extends AbstractConnectorMain {

    public static void main(String[] argv) {

        CommandLine cmd = parseCommand(argv);
        Map<String, String> connectInfo = constructConfig(cmd);
        AMQPConnector amqpConnector = new AMQPConnector(connectInfo.get(VANTIQ_SOURCE_NAME), connectInfo);
        amqpConnector.start();
    }

}
