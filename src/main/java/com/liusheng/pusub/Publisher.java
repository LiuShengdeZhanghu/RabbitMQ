package com.liusheng.pusub;

import com.liusheng.config.RabbitMQClient;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Publisher {

    public void publish() throws IOException, TimeoutException {
        Connection connection = RabbitMQClient.getConnection();

        // 创建Channel
        Channel channel = connection.createChannel();

        // 创建exchange，绑定一个队列
        // 参数1：exchange的名称
        // 参数2：指定exchange的类型，FANOUT - pubsub； DIRECT - Routing； Topic - Topics
        channel.exchangeDeclare("pubsub-exchange", BuiltinExchangeType.FANOUT);
        channel.queueBind("pubsub-queue1", "pubsub-exchange", "");
        channel.queueBind("pubsub-queue2", "pubsub-exchange", "");

        // 发布消息exchange，同时指定路由的规则
        for (int i = 0; i <10 ; i++) {
            String msg = "Hello-Work: "+i;
            channel.basicPublish("pubsub-exchange","",null, msg.getBytes());
        }
        // PS: exchange不会把数据进行持久化，Queue才会把数据进行持久化，如果消息没有进入队列，则会丢失

        System.out.println("已经发布一条数据");
        // 释放资源
        channel.close();
        connection.close();
    }
}
