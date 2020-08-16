package com.liusheng.routing;

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
        channel.exchangeDeclare("routing-exchange", BuiltinExchangeType.DIRECT);
        // 绑定队列并指定映射规第三个参数便是映射规则
        channel.queueBind("routing-queue-error", "routing-exchange", "ERROR");
        channel.queueBind("routing-queue-info", "routing-exchange", "INFO");

        // 发布消息exchange，同时指定路由的规则
        for (int i = 0; i <10 ; i++) {
            String msg = "Hello-routing: "+i;
            if(i % 2 == 0){
                String text = msg+" INFO";
                channel.basicPublish("routing-exchange","INFO",null, text.getBytes());
            }else {
                String text = msg+" ERROR";
                channel.basicPublish("routing-exchange","ERROR",null, msg.getBytes());
            }
        }
        // PS: exchange不会把数据进行持久化，Queue才会把数据进行持久化，如果消息没有进入队列，则会丢失

        System.out.println("已经发布一条数据");
        // 释放资源
        channel.close();
        connection.close();
    }
}
