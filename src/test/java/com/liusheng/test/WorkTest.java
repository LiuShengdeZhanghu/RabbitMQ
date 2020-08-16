package com.liusheng.test;

import com.liusheng.work.Consumer1;
import com.liusheng.work.Consumer2;
import com.liusheng.work.Publisher;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WorkTest {

    @Test
    public void p2() throws IOException, TimeoutException {
        Publisher publisher = new Publisher();
        publisher.publish();
    }

    @Test
    public void c1() throws IOException, TimeoutException {
        Consumer1 consumer1 = new Consumer1();
        consumer1.runConsumer();
    }

    @Test
    public void c2() throws IOException, TimeoutException {
        Consumer2 consumer2 = new Consumer2();
        consumer2.runConsumer();
    }
}
