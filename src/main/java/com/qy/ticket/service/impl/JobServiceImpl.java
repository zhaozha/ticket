package com.qy.ticket.service.impl;

import com.qy.ticket.dao.VTicketMapper;
import com.qy.ticket.entity.VTicket;
import com.qy.ticket.service.JobService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhaozha
 * @date 2020/2/26 下午9:12
 */
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
      RAtomicLong rAtomicLong =
          redissonSingle.getAtomicLong(
              "gate-seq-pdId"
                  + vTicket.getProductId()
                  + "-paId"
                  + vTicket.getParkId()
                  + "-time"
                  + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
      rAtomicLong.delete();
    }
  }
}
