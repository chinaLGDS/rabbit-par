package com.bfxy.rabbit.producer.broker;

import com.bfxy.rabbit.api.Message;
import com.bfxy.rabbit.api.MessageType;
import com.bfxy.rabbit.producer.constant.BrokerMessageConst;
import com.bfxy.rabbit.producer.constant.BrokerMessageStatus;
import com.bfxy.rabbit.producer.entity.BrokerMessage;
import com.bfxy.rabbit.producer.service.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class RabbitBroekrImpl implements RabbitBroker{

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    /**
     * $sendKernel 发送消息的核心方法，使用异步线程池进行发送消息
     * @param message
     */
    private void sendKernel(Message message){

        AsyncBaseQueue.submit((Runnable) () ->{
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s",
                    message.getMessageId(),
                    System.currentTimeMillis()));
            String topic = message.getTopic();
            String routingKey = message.getRoutingKey();
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());

        });

    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);

    }

    @Override
    public void reliantSend(Message message) {
        //1. 把数据库的消息发送日志先记录好
        Date now = new Date();
        BrokerMessage brokerMessage = new BrokerMessage();
        brokerMessage.setMessageId(message.getMessageId());
        brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
        //tryCount在最开始发送的时候不需要进行设置
        brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
        brokerMessage.setCreateTime(now);
        brokerMessage.setUpdateTime(now);
        brokerMessage.setMessage(message);
        messageStoreService.insert(brokerMessage);
        //2.执行真正的发送消息逻辑
        sendKernel(message);

    }

    @Override
    public void sendMessage() {

    }
}
