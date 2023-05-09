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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Indexed;

/**
 * redis 单点配置类
 */

@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.redis", name = "host")
@Indexed
public class StandaloneLettuceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneLettuceAutoConfiguration.class);

    @Bean
    @ConditionalOnClass({LettuceConnectionFactory.class})
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        factory.isClusterAware();
        logger.info("#############【Initializing LettucePool start......】###########");
        logger.info("#############【redis url】###########:{}", factory.getHostName());
        logger.info("#############【redis port】###########:{}", factory.getPort());
        logger.info("#############【redis clientName】###########:{}", factory.getClientName());
        logger.info("#############【redis password】###########:{}", factory.getPassword());
        // 每次获取连接时，校验连接是否可用。默认false,不去校验。默认情况下，lettuce开启一个共享的物理连接，是一个长连接，
        // 所以默认情况下是不会校验连接是否可用的。如果设置true,会导致性能下降
//        factory.setValidateConnection(false);
        // 这个属性默认是true,允许多个连接公用一个物理连接。
        // 如果设置false ,每一个连接的操作都会开启和关闭socket连接,会导致性能下降
//        factory.setShareNativeConnection(true);
        RedisTemplate template = new RedisTemplate();
        // 配置连接工厂
        template.setConnectionFactory(factory);
        //使用Jackson2JsonRedisSerializer替换默认的JdkSerializationRedisSerializer来序列化和反序列化redis的value值
        ObjectMapper mapper = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(mapper, Object.class);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        logger.info("#############【Initializing LettucePool end......】###########");

        return template;
    }

}
