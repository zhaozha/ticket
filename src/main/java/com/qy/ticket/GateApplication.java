package com.qy.ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages="com.qy.ticket")
@EnableScheduling
public class GateApplication {

  public static void main(String[] args) {
    SpringApplication.run(GateApplication.class, args);
  }
}
