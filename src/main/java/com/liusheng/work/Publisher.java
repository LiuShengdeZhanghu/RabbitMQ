package com.liusheng.work;

import com.liusheng.config.RabbitMQClient;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Publisher {

    public void publish() throws IOException, TimeoutException {
        Connection connection = RabbitMQClient.getConnection();

        // 创建Channel
        Channel channel = connection.createChannel();

        // 发布消息exchange，同时指定路由的规则
        for (int i = 0; i <10 ; i++) {
            String msg = "Hello-Work: "+i;
            channel.basicPublish("","work",null, msg.getBytes());
        }
        // PS: exchange不会把数据进行持久化，Queue才会把数据进行持久化，如果消息没有进入队列，则会丢失

        System.out.println("已经发布一条数据");
        // 释放资源
        channel.close();
        connection.close();
    }
}
