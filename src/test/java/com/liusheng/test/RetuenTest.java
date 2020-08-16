package com.liusheng.test;

import com.liusheng.confirm.Publisher;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RetuenTest {

    @Test
    public void demo1() throws InterruptedException, TimeoutException, IOException {
        Publisher publisher = new Publisher();
        publisher.publish();
    }
}
