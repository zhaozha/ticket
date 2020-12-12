package com.qy.ticket.service.impl;

import com.qy.ticket.constant.RedisConstant;
import com.qy.ticket.dao.VTicketMapper;
import com.qy.ticket.entity.VTicket;
import com.qy.ticket.service.JobService;
import com.qy.ticket.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.qy.ticket.constant.RedisConstant.KEY_TICKET_SEQ;

/**
 * @author zhaozha
 * @date 2020/2/26 下午9:12
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JobServiceImpl implements JobService {
    private final RedissonClient redissonSingle;
    private final VTicketMapper vTicketMapper;

    @Override
    @Scheduled(cron = "0 50 23 * * ?")
    public void removeSeq() {
        List<VTicket> vTickets = vTicketMapper.selectAll();
        for (VTicket vTicket : vTickets) {
            Long parkId = vTicket.getParkId();
            Long productId = vTicket.getProductId();
            RAtomicLong rAtomicLong = redissonSingle.getAtomicLong(RedisConstant.concat(KEY_TICKET_SEQ, parkId.toString(), productId.toString(), DateUtil.yyyyMMdd.format(new Date())));
            if (rAtomicLong.isExists()) {
                rAtomicLong.delete();
            }
        }
        log.info("清除票务当日seq成功");
    }
}
