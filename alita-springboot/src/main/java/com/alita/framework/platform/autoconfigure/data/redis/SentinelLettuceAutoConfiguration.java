package com.alita.framework.platform.autoconfigure.data.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Indexed;


/**
 * redis 哨兵配置类
 */


@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.redis.sentinel", name = "nodes")
@Indexed
public class SentinelLettuceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneLettuceAutoConfiguration.class);

    @Bean
    @ConditionalOnClass({LettuceConnectionFactory.class})
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {

        logger.info("#############【Initializing LettucePool start......】###########");
        logger.info("#############【redis url】###########:{}", factory.getHostName());
        logger.info("#############【redis port】###########:{}", factory.getPort());
        logger.info("#############【redis clientName】###########:{}", factory.getClientName());
        logger.info("#############【redis password】###########:{}", factory.getPassword());
        RedisTemplate template = new RedisTemplate();
        template.setConnectionFactory(factory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(mapper, Object.class);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        logger.info("#############【Initializing LettucePool end......】###########");

        return template;
    }

}
