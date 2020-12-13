package com.qy.ticket.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaozha
 * @date 2020/1/6 下午1:14
 */
@Configuration
public class RedissonConfiguration {
  @Value("${redisson.address}")
  private String address;
  @Value("${redisson.password}")
  private String password;
  @Value("${redisson.timeout}")
  private Integer timeout;
  @Value("${redisson.connectionPoolSize}")
  private Integer connectionPoolSize;
  @Value("${redisson.connectionMinimumIdleSize}")
  private Integer connectionMinimumIdleSize;

  @Bean
  RedissonClient redissonSingle() {
    Config config = new Config();
    config
            .useSingleServer()
            .setAddress(address)
            .setPassword(password)
            .setTimeout(timeout)
            .setConnectionPoolSize(connectionPoolSize)
            .setConnectionMinimumIdleSize(connectionMinimumIdleSize);
    return Redisson.create(config);
  }
}
