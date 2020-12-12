package com.qy.ticket.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaozha
 * @date 2020/1/6 下午1:14
 */
@Configuration
public class RedissonConfiguration {
  @Bean
  RedissonClient redissonSingle() {
    Config config = new Config();
    config
        .useSingleServer()
       .setAddress("redis://r-wz91r86qha12v64mo7pd.redis.rds.aliyuncs.com:6379")
        //    .setAddress("redis://r-wz91r86qha12v64mo7pd.redis.rds.aliyuncs.com:6379")
        .setPassword("210*2462fqy168_c922b2a1ea504b")
        .setTimeout(3000)
        .setConnectionPoolSize(150)
        .setConnectionMinimumIdleSize(20);
    return Redisson.create(config);
  }
}
