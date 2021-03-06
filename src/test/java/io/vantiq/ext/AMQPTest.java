package io.vantiq.ext;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AMQPTest {

    private static final Logger LOG = LoggerFactory.getLogger(AMQPTest.class);

    @Test
    public void test1() throws IOException {

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        // if need password
//        connectionFactory.setUsername("the_user");
//        connectionFactory.setPassword("amqpPassword");
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(new Queue("test_name"));
        AmqpTemplate template = new RabbitTemplate(connectionFactory);


        Map data = new HashMap();
        data.put("k1", "kivalue");
        data.put("k2", "k2value");

        template.convertAndSend("test_name", data);
//        String foo = (String) template.receiveAndConvert("myqueue");
//        LOG.debug("Result: {}", foo);

    }


    @Test
    public void testProtoBuf() {
        String msg = "ClYSDQjjDxAIGAwgEygaMCUaFDQ0MDYwNTAzMDAxMzI2OTY0NjU2Ig4xOTIuMTY4LjEyLjE4MSoOMTkyLjE2OC4xMi4xODE17griQj1Q7bhBRQAAgD9IARgB\n" +
                "IAEoATD///////////8BOP///////////wFCFA0bL10+FcHK4T4dzczMPSVYOTQ+WktodHRwOi8vMTkyLjE2OC4xMi42MDo4MC9ncm91cDIvTTAwLzAwLzY1\n" +
                "L3dLZ01QbDFSVFAyQUNEb1FBQWFhWEdKX3pCbzc3Ni5qcGdaS2h0dHA6Ly8xOTIuMTY4LjEyLjYwOjgwL2dyb3VwMi9NMDAvMDAvNjUvd0tnTVBsMVJUUDJB\n" +
                "SUJmMUFBQWotUFk2QzhNNDUyLmpwZw==";


        String protoFile = "face.proto";
        String javaFile = "Face.java";



        ConnectionFactory connectionFactory = new CachingConnectionFactory();
        AmqpAdmin admin = new RabbitAdmin(connectionFactory);
        admin.declareQueue(new Queue("myqueue"));
        AmqpTemplate template = new RabbitTemplate(connectionFactory);
        template.convertAndSend("myqueue", "foo");
        String foo = (String) template.receiveAndConvert("myqueue");

        LOG.debug("Result: {}", foo);


    }

    @Test
    public void testByte() {
        Object foo = "foo";
        byte[] bar = "bar".getBytes();
        if (foo instanceof byte[]) {
            LOG.debug("{} is bytes", foo);
        }
        if (bar instanceof byte[]) {
            LOG.debug("{} is bytes", bar);
        }
    }
}
