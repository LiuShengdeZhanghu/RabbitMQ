package com.liusheng.confirm;

import com.liusheng.config.RabbitMQClient;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Publisher {

    public void publish() throws IOException, TimeoutException, InterruptedException {
        Connection connection = RabbitMQClient.getConnection();

        // 创建Channel
        Channel channel = connection.createChannel();

        // 开启return机制
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                // 当消息没有送达到队列的时候才会执行
                System.out.println(new String(bytes, StandardCharsets.UTF_8) + "没有送达到队列中");
            }
        });

        // 开启confirm
        channel.confirmSelect();

        // 通过confirm返回的结果判断是否发送成功
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean b) throws IOException {
                System.out.println("消息发送成功！标识："+deliveryTag+" 是否是批量操作："+b);
            }

            @Override
            public void handleNack(long deliveryTag, boolean b) throws IOException {
                System.out.println("消息发送失败！");
            }
        });

        // 给消息设置一个id，以便作为添加到redis的key
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .deliveryMode(1)    // 指定消息是否需要持久化，1表示需要
                .messageId(UUID.randomUUID().toString())   // 给消息一个id
                .build();

        String msg = "Hello-Word";
        // 这里的第三个参数设置为true后，return机制才会生效
        channel.basicPublish("","helloword",true, properties, msg.getBytes());

        // PS: exchange不会把数据进行持久化，Queue才会把数据进行持久化，如果消息没有进入队列，则会丢失


        System.out.println("已经发布一条数据");
        // 释放资源
        System.in.read();
        channel.close();
        connection.close();
    }
}
