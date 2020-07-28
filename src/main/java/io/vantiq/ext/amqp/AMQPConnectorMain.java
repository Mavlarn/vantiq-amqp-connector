package io.vantiq.ext.amqp;


public class AMQPConnectorMain {

    public static void main(String[] argv) {

        AMQPConnector connector = new AMQPConnector();
        connector.start();
    }

}
