package com.genius.spring.config;

import com.genius.spring.entity.Order;
import com.genius.spring.entity.Packaged;

import java.io.File;
import java.util.Map;

public class MessageDelegate {

    //默认的方法
    public void handleMessage(byte[] messageBody) {
        System.err.println("默认方法， 消息内容：" + new String(messageBody));
    }

    public void handleMessage(String messageBody) {
        System.err.println("默认方法， 消息内容：" + messageBody);
    }

    public void consumeMessage(byte[] messageBody) {
        System.err.println("字节数组方法， 消息内容：" + new String(messageBody));
    }

    public void consumeMessage(String messageBody) {
        System.err.println("字符串方法， 消息内容：" + messageBody);
    }

    public void method1(String messageBody) {
        System.err.println("method01消息内容：" + messageBody);
    }

    public void method2(String messageBody) {
        System.err.println("method02消息内容：" + messageBody);
    }

    public void consumeMessage(Map messageBody) {
        System.err.println("map 方法，消息内容：" + messageBody);
    }

    public void consumeMessage(Order order) {
        System.err.println("order对象，消息内容：" + order.toString());
    }

    public void consumeMessage(Packaged packaged) {
        System.err.println("Packaged对象，消息内容：" + packaged.toString());
    }

    public void consumeMessage(File file) {
        System.err.println("文件对象，消息内容：" + file.getName());
    }

}
