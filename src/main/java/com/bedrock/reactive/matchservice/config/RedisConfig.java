package com.bedrock.reactive.matchservice.config;

import com.bedrock.reactive.matchservice.model.Match;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  @Value("${spring.redis.hostname}")
  private String hostname;

  @Value("${spring.redis.port}")
  private Integer port;

//	@Value("${spring.redis.password}")
//	String password;

  private final ObjectMapper objectMapper;

  @Bean
  ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
    var redisConfig = new RedisStandaloneConfiguration(hostname, port);
//    redisConfig.setPassword(password);
    return new LettuceConnectionFactory(redisConfig);
  }

  @Bean
  ReactiveRedisTemplate<String, Match> matchReactiveRedisTemplate(
      ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    var serializationContext = RedisSerializationContext
        .<String, Match>newSerializationContext(new StringRedisSerializer())
        .hashKey(new StringRedisSerializer())
        .hashValue(configureJackson2JsonRedisSerializer(Match.class))
        .build();

    return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
  }

  <T>Jackson2JsonRedisSerializer<T> configureJackson2JsonRedisSerializer(Class<T> t) {
    var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(t);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

    return jackson2JsonRedisSerializer;
  }

}
