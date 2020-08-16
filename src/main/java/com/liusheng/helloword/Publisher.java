package com.liusheng.helloword;

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
        String msg = "Hello-Word";
        // 发布消息exchange，同时指定路由的规则
        // 参数1：指定exchange，""，空字符串代表使用默认的值
        // 参数2：指定路由的规则，使用具体的队列名称
        // 参数3：指定传递的消息所携带的properties（属性）
        // 参数4：指定发布的具体消息，byte[]类型
        channel.basicPublish("","helloword",null, msg.getBytes());
        // PS: exchange不会把数据进行持久化，Queue才会把数据进行持久化，如果消息没有进入队列，则会丢失

        System.out.println("已经发布一条数据");
        // 释放资源
        channel.close();
        connection.close();
    }
}
