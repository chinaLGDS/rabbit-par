package com.bfxy.rabbit.producer.broker;

import com.bfxy.rabbit.api.Message;
import com.bfxy.rabbit.api.MessageType;
import com.bfxy.rabbit.api.exception.MessageRunTimeException;
import com.bfxy.rabbit.common.convert.RabbitMessageConverter;
import com.bfxy.rabbit.common.serializer.Serializer;
import com.bfxy.rabbit.common.serializer.SerializerFactory;
import com.bfxy.rabbit.common.serializer.impl.JacksonSerializerFactory;
import com.bfxy.rabbit.producer.service.MessageStoreService;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bfxy.rabbit.common.convert.GenericMessageConvert;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 *  $RabbitTemplateContainer 池化封装
 *  每一个topic对应一个RabbitTemplate
 *  1.提高发送效率
 *  2.根据不同需求制定化不同的rabbitTemplate,比如每一个topic 都有自己的routingKey规则
 * @author nly
 */
@Slf4j
@Component
public class RabbitTemplateContainer implements  RabbitTemplate.ConfirmCallback {

    private Map<String /* TOPIC */, RabbitTemplate> rabbitMap = Maps.newConcurrentMap();

    private Splitter splitter = Splitter.on("#");

    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private ConnectionFactory connectionFactory;

    public RabbitTemplate getTemplate(Message message) throws MessageRunTimeException{
        Preconditions.checkNotNull(message);
        String topic = message.getTopic();
        RabbitTemplate rabbitTemplate = rabbitMap.get(topic);
        if (rabbitTemplate != null){
            return rabbitTemplate;
        }
        log.info("#RabbitTemplateContainer.getTemplate# topic: {} is not exists, create one", topic);

        RabbitTemplate newTemplate = new RabbitTemplate(connectionFactory);
        newTemplate.setExchange(topic);
        newTemplate.setRoutingKey(message.getRoutingKey());
        newTemplate.setRetryTemplate(new RetryTemplate());

        //添加序列化和反序列化对象以及convert对象
        Serializer serializer = serializerFactory.create();
        GenericMessageConvert gmc = new GenericMessageConvert(serializer);
        RabbitMessageConverter rmc = new RabbitMessageConverter(gmc);
        newTemplate.setMessageConverter(rmc);
        //对于message的序列化方式
        //newTemplate.setMessageConverter(messageConverter);

        String messageType = message.getMessageType();
        if(!MessageType.RAPID.equals(messageType)) {
            newTemplate.setConfirmCallback(this);
            // 开启事务 newTemplate.setChannelTransacted(true);
        }

        rabbitMap.putIfAbsent(topic, newTemplate);

        return rabbitMap.get(topic);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        // 	具体的消息应答
        List<String> strings = splitter.splitToList(correlationData.getId());
        String messageId = strings.get(0);
        long sendTime = Long.parseLong(strings.get(1));;
        if(ack) {

            this.messageStoreService.succuess(messageId);
            //	当Broker 返回ACK成功时, 就是更新一下日志表里对应的消息发送状态为 SEND_OK

            /*// 	如果当前消息类型为reliant 我们就去数据库查找并进行更新
            if(MessageType.RELIANT.endsWith(messageType)) {
                this.messageStoreService.succuess(messageId);
            }*/
            log.info("send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            log.error("send message is Fail, confirm messageId: {}, sendTime: {}", messageId, sendTime);

        }

    }
}
