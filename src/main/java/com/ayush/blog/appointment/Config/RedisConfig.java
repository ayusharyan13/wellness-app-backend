package com.ayush.blog.appointment.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling() // if you want to use connection pooling
                .build();

        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName("redis-17989.c264.ap-south-1-1.ec2.redns.redis-cloud.com");
        redisStandaloneConfig.setPort(17989);
        redisStandaloneConfig.setUsername("default");
        redisStandaloneConfig.setPassword(RedisPassword.of("VjDIjcte5WgqdTocFDJ5umZZprYjjClT"));
//        redisStandaloneConfig.setSsl(true);

        return new JedisConnectionFactory(redisStandaloneConfig, clientConfig);
    }
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
