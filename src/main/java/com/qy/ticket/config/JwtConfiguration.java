package com.qy.ticket.config;

import com.qy.ticket.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/18 下午12:01
 **/
@Configuration
public class JwtConfiguration {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-time-in-second}")
    private Long expirationTimeInSecond;

    @Bean
    public JwtUtil jwtOperator() {
        return new JwtUtil(secret, expirationTimeInSecond);
    }
}