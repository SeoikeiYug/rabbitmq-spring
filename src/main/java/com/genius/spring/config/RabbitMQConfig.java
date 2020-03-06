package com.genius.spring.config;

import com.genius.spring.convert.ImageMessageConverter;
import com.genius.spring.convert.PDFMessageConverter;
import com.genius.spring.convert.TextMessageConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.DefaultJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.soap.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@ComponentScan({"com.genius.spring.*"})
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setAddresses("111.230.246.199:5672");
        cachingConnectionFactory.setUsername("guest");
        cachingConnectionFactory.setPassword("guest");
        cachingConnectionFactory.setVirtualHost("/");
        return cachingConnectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(final ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 针对消费者配置
     * 1.设置交换机类型
     * 2.将队列绑定到交换机
     *
     * FanoutExchange：将消息分发到所有的绑定队列，无routingKey的概念
     * HeadersExchange：通过添加属性key-value匹配
     * DirectExchange：将routingKey分发到指定队列
     * TopicExchange：多关键字匹配
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }

    @Bean
    public Queue queue001() {
        //durable 队列持久
        return new Queue("queue001", true);
    }

    @Bean
    public Binding binding001() {
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }

    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue002() {
        //durable 队列持久
        return new Queue("queue002", true);
    }

    @Bean
    public Binding binding002() {
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }

    @Bean
    public Queue queue003() {
        //durable 队列持久
        return new Queue("queue003", true);
    }

    @Bean
    public Binding binding003() {
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image() {
        return new Queue("image_queue", true);
    }

    @Bean
    public Queue queue_pdf() {
        return new Queue("pdf_queue", true);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        simpleMessageListenerContainer.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
        simpleMessageListenerContainer.setExposeListenerChannel(true);
        simpleMessageListenerContainer.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID().toString());

        //消息监听
//        simpleMessageListenerContainer.setupMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                String msg = new String(message.getBody());
//                System.err.println("---消费者: " + msg);
//            }
//        });

        //适配器模式
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//        messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
//        messageListenerAdapter.setMessageConverter(new TextMessageConverter());
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //适配器模式：队列和方法名称也可以一一对应
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//        messageListenerAdapter.setMessageConverter(new TextMessageConverter());
//
//        Map<String, String> queueOrTagToMethodName = new HashMap<>();
//        queueOrTagToMethodName.put("queue001", "method1");
//        queueOrTagToMethodName.put("queue002", "method2");
//        messageListenerAdapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //1.1支持json格式的转换器
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//        messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //1.2支持java对象转换
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//        messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
//
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        jackson2JsonMessageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
//
//        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //1.3支持java对象多映射转换
//        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
//        messageListenerAdapter.setDefaultListenerMethod("consumeMessage");
//
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper defaultJackson2JavaTypeMapper = new DefaultJackson2JavaTypeMapper();
//
//        Map<String, Class<?>> idClassMapping = new HashMap<>();
//        idClassMapping.put("order", com.genius.spring.entity.Order.class);
//        idClassMapping.put("packaged", com.genius.spring.entity.Packaged.class);
//        defaultJackson2JavaTypeMapper.setIdClassMapping(idClassMapping);
//
//        jackson2JsonMessageConverter.setJavaTypeMapper(defaultJackson2JavaTypeMapper);
//
//        messageListenerAdapter.setMessageConverter(jackson2JsonMessageConverter);
//        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        //全局转换器
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(new MessageDelegate());
        messageListenerAdapter.setDefaultListenerMethod("consumeMessage");

        ContentTypeDelegatingMessageConverter contentTypeDelegatingMessageConverter = new ContentTypeDelegatingMessageConverter();

        TextMessageConverter textMessageConverter = new TextMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("text", textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("html/text", textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("xml/text", textMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("text/plain", textMessageConverter);

        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("json", jackson2JsonMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("application/json", jackson2JsonMessageConverter);

        ImageMessageConverter imageMessageConverter = new ImageMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("image/png", imageMessageConverter);
        contentTypeDelegatingMessageConverter.addDelegate("image", imageMessageConverter);

        PDFMessageConverter pdfMessageConverter = new PDFMessageConverter();
        contentTypeDelegatingMessageConverter.addDelegate("application/pdf", pdfMessageConverter);

        messageListenerAdapter.setMessageConverter(contentTypeDelegatingMessageConverter);

        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);


        return simpleMessageListenerContainer;
    }

}
