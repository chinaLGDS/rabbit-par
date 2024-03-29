package com.bfxy.rabbit.common.convert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
//import com.bfxy.rabbit.common.convert.GenericMessageConvert

import com.google.common.base.Preconditions;

/**
 * 	$RabbitMessageConverter
 * @author Alienware
 *
 */
public class RabbitMessageConverter implements MessageConverter {

	private GenericMessageConvert delegate;
	
//	private final String delaultExprie = String.valueOf(24 * 60 * 60 * 1000);
	
	public RabbitMessageConverter(GenericMessageConvert genericMessageConverter) {
		Preconditions.checkNotNull(genericMessageConverter);
		this.delegate = genericMessageConverter;
	}

	//定义相关的配置信息
	@Override
	public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
//		messageProperties.setExpiration(delaultExprie);
//		com.bfxy.rabbit.api.Message message = (com.bfxy.rabbit.api.Message)object;
//		messageProperties.setDelay(message.getDelayMills());
		return this.delegate.toMessage(object, messageProperties);
	}

	@Override
	public Object fromMessage(Message message) throws MessageConversionException {
		com.bfxy.rabbit.api.Message msg = (com.bfxy.rabbit.api.Message) this.delegate.fromMessage(message);
		return msg;
	}

}
