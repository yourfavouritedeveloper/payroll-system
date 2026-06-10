package org.example.employeems.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        System.out.println("=== REDIS CONFIG: Creating JedisConnectionFactory ===");
        System.out.println("=== HOST: " + host);
        System.out.println("=== PORT: " + port);

        try {
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
            configuration.setHostName(host);
            configuration.setPort(port);
            System.out.println("=== REDIS CONFIG: RedisStandaloneConfiguration created ===");

            JedisConnectionFactory factory = new JedisConnectionFactory(configuration);
            System.out.println("=== REDIS CONFIG: JedisConnectionFactory created successfully ===");
            return factory;
        } catch (Exception e) {
            System.out.println("=== REDIS CONFIG FAILED: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        System.out.println("=== REDIS CONFIG: Creating RedisTemplate ===");

        try {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(jedisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new JdkSerializationRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new JdkSerializationRedisSerializer());
            template.setEnableTransactionSupport(true);
            template.afterPropertiesSet();
            System.out.println("=== REDIS CONFIG: RedisTemplate created successfully ===");
            return template;
        } catch (Exception e) {
            System.out.println("=== REDIS CONFIG FAILED at RedisTemplate: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}

