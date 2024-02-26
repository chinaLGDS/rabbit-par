package com.bfxy.rabbit.common.convert;

import com.bfxy.rabbit.common.serializer.Serializer;
import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * $GenericMessageConvert 真实对象
 * @author nly
 */
public class GenericMessageConvert implements MessageConverter {

    private Serializer serializer;

    public GenericMessageConvert(Serializer serializer){
        Preconditions.checkNotNull(serializer);
        this.serializer = serializer;
    }

    //下面两个方法主要是将org.springframework.messaging转换成我们这里com.bfxy.rabbit.api下的message
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new org.springframework.amqp.core.Message(this.serializer.serializeRaw(object), messageProperties);
    }

    //我们需要做的事情是将传入来的对象(我们自己定义在com.bfxy.rabbit.api里的message)跟springframework.amqp.core.Message进行转换。
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return this.serializer.deserialize(message.getBody());
    }



}
