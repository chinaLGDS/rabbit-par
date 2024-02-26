package com.bfxy.rabbit.producer.broker;

import com.bfxy.rabbit.api.Message;

/**
 * $RabbitBroker
 * @author nly
 */
public interface RabbitBroker {

    void rapidSend(Message message);

    void confirmSend(Message message);

    void reliantSend(Message message);

    void sendMessage();
}
