package com.liusheng.test;

import com.liusheng.config.RabbitMQClient;
import com.liusheng.helloword.Consumer;
import com.liusheng.helloword.Publisher;
import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MyTest {

    @Test
    public void demo1() throws IOException {
        Connection connection = RabbitMQClient.getConnection();
        connection.close();
    }

    @Test
    public void p() throws IOException, TimeoutException {
        Publisher publisher = new Publisher();
        publisher.publish();
    }

    @Test
    public void c() throws IOException, TimeoutException {
        Consumer consumer = new Consumer();
        consumer.runConsumer();
    }

}
