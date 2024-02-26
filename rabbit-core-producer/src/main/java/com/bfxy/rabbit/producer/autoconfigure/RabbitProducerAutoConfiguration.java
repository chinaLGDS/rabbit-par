package com.bfxy.rabbit.producer.autoconfigure;

import com.bfxy.rabbit.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * $RabbitProdecerAutoConfiguration  自动装配
 * @author nly
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.bfxy.rabbit.producer.*"})
public class RabbitProducerAutoConfiguration {
}
