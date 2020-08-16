package com.liusheng.topic;

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
        channel.exchangeDeclare("topic-exchange", BuiltinExchangeType.TOPIC);
        // 绑定队列并指定映射规第三个参数便是映射规则
        // 这里的路由规则是根据关注的角度进行绑定，比如一个动物的信息
        // 动物的信息 <speed><color><what>
        // *.red.*     表示只关注颜色信息red，*代表占位符
        // fast.#      表示只关注速度，#代表通配符
        // #.rabbit / *.*.rabbit    表示只关注是什么动物
        channel.queueBind("topic-queue-1", "topic-exchange", "*.red.*");
        channel.queueBind("topic-queue-2", "topic-exchange", "fast.*.*");
        channel.queueBind("topic-queue-2", "topic-exchange", "*.*.rabbit ");

        // 发布消息exchange，同时指定路由的规则，根据路由匹配进行发送消息，第一个能到发到所有的队列中
        channel.basicPublish("topic-exchange","fast.red.monkey",null, "快红猴".getBytes());
        // 这个所有的队列路由都不能匹配上，不能发到任何的队列中
        channel.basicPublish("topic-exchange","slow.black.dog",null, "慢黑狗".getBytes());
        // 这个可以匹配到最后一个路由规则，能发到第二个队列中
        channel.basicPublish("topic-exchange","slow.black.rabbit",null, "慢黑兔".getBytes());
        channel.basicPublish("topic-exchange","fast.white.cat",null, "快白猫".getBytes());
        // PS: exchange不会把数据进行持久化，Queue才会把数据进行持久化，如果消息没有进入队列，则会丢失

        System.out.println("已经发布一条数据");
        // 释放资源
        channel.close();
        connection.close();
    }
}
