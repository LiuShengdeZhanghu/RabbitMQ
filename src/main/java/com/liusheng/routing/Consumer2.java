package com.liusheng.routing;

import com.liusheng.config.RabbitMQClient;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer2 {


    public void runConsumer() throws IOException, TimeoutException {
        Connection connection = RabbitMQClient.getConnection();

        // 创建Channel，连接队列
        Channel channel = connection.createChannel();

        // 声明队列
        channel.queueDeclare("routing-queue-error",true, false, false, null);
        // 声明当前消费者，一次消费多少个消息
        channel.basicQos(1);

        // 开启监听队列
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("消费者ERROR接收到消息："+new String(body, StandardCharsets.UTF_8));
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 手动回调给RabbitMQ，通知消息队列已经处理完毕，准备获取下一个消息
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        // 参数1：指定队列名称
        // 参数2：是否自动回调（接收到消息后，会立即告诉RabbitMQ）
        // 参数3：指定消费回调
        channel.basicConsume("routing-queue-error",false,consumer);

        System.out.println("消费者ERROR开始监听队列！");
        // System.in.read()
        System.in.read();
        // 释放资源
        channel.close();
        connection.close();
    }
}
