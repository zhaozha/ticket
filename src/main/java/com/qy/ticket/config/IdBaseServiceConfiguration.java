package com.qy.ticket.config;

import com.virgo.virgoidgenerator.enums.IdType;
import com.virgo.virgoidgenerator.factory.IdServiceFactoryBean;
import com.virgo.virgoidgenerator.intf.IdBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaozha
 * @date 2020/1/6 下午1:13
 */
@Slf4j
@Configuration
public class IdBaseServiceConfiguration {
    @Value("${id-generator.machineId}")
    private Long machineId;

    @Value("${id-generator.dataCenterId}")
    private Long dataCenterId;

    @Bean
    public IdBaseService idBaseService() {
        IdServiceFactoryBean idServiceFactoryBean =
                IdServiceFactoryBean.builder()
                        .version(0)
                        .type(IdType.LEVEL_MILLISECOND.value())
                        .dataCenterId(dataCenterId)
                        .machineId(machineId)
                        .build();
        try {
            return idServiceFactoryBean.getObject();
        } catch (Exception e) {
            log.error("Id Generator Init Error.");
            e.printStackTrace();
        }
        return null;
    }
}
