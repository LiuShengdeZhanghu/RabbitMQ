package com.liusheng.helloword;

import com.liusheng.config.RabbitMQClient;
import com.rabbitmq.client.*;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Consumer {


    public void runConsumer() throws IOException, TimeoutException {
        Connection connection = RabbitMQClient.getConnection();

        // 创建Channel，连接队列
        Channel channel = connection.createChannel();

        // 声明队列
        // 参数1：队列名称
        // 当前队列是否需要持久化
        // 参数3：是否排外（connect.close() - 当前队列会被自动删除，当前队列只能被一个消费者消费）
        // 参数4：如果这个队列没有消费在消费，队列自动删除
        // 参数5：指定当前队列的其他信息（存储的最大内容）
        channel.queueDeclare("helloword",true, false, false, null);

        // 开启监听队列
        DefaultConsumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Jedis jedis = new Jedis("127.0.0.1",6379);
                String messageId = properties.getMessageId();

                // setnx到redis中，默认制定vlue为0，EX表示生存时间，10表示10秒
                String result = jedis.set(messageId, "0", "NX","EX",10);
                if (result != null && "OK".equalsIgnoreCase(result)){
                    System.out.println("接收到消息："+new String(body, StandardCharsets.UTF_8));

                    // 消费成功，把redis中的对应的key的值设置为1
                    jedis.set(messageId,"1");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }else {
                    // 如果setnx失败，则获取key对应的value，如果是0，则返回函数，如果是1就执行ack
                    String value = jedis.get(messageId);
                    if ("1".equalsIgnoreCase(value)) {
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            }
        };
        // 参数1：指定队列名称
        // 参数2：是否自动回调（接收到消息后，会立即告诉RabbitMQ）
        // 参数3：指定消费回调
        channel.basicConsume("helloword",true,consumer);

        System.out.println("消费者开始监听队列！");
        // System.in.read()
        System.in.read();
        // 释放资源
        channel.close();
        connection.close();
    }
}
