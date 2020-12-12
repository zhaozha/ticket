package com.qy.ticket.dao;

import com.qy.ticket.entity.TblRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TblRecordMapper extends Mapper<TblRecord> {
  @Select(
      "select sum(amount) as amount,sum(refund_amount) as refundAmount,sum(income) as income,sum(effective_num) as effectiveNum"
          + " from tbl_record"
          + " where time >= #{startTime}"
          + " and time <= #{endTime}"
          + " and park_id = #{parkId}"
          + " and product_id =#{productId}")
  TblRecord Sum(
      @Param("startTime") String startTime,
      @Param("endTime") String endTime,
      @Param("parkId") Long parkId,
      @Param("productId") Long productId);

  @Select(
      "select sum(amount) as amount,sum(refund_amount) as refundAmount,sum(income) as income,sum(effective_num) as effectiveNum,DATE_FORMAT( time, '%Y-%m-%d' ) AS time"
          + " from tbl_record"
          + " where time >= #{startTime}"
          + " and time <= #{endTime}"
          + " and park_id = #{parkId}"
          + " and product_id =#{productId}"
          + " GROUP BY"
          + " DATE_FORMAT( time, '%Y-%m-%d' ) order by DATE_FORMAT( time, '%Y-%m-%d' ) DESC")
  List<TblRecord> Day(
      @Param("startTime") String startTime,
      @Param("endTime") String endTime,
      @Param("parkId") Long parkId,
      @Param("productId") Long productId);

  @Select(
          "select sum(amount) as amount,sum(refund_amount) as refundAmount,sum(income) as income,sum(effective_num) as effectiveNum,DATE_FORMAT( time, '%Y-%m-%d' ) AS time"
                  + " from tbl_record"
                  + " where time >= #{startTime}"
                  + " and time <= #{endTime}"
                  + " and park_id = #{parkId}"
                  + " and product_id =#{productId}"
                  + " GROUP BY "
                  + " DATE_FORMAT( time, '%Y-%m' ) order by DATE_FORMAT( time, '%Y-%m' ) DESC")
  List<TblRecord> Month(
          @Param("startTime") String startTime,
          @Param("endTime") String endTime,
          @Param("parkId") Long parkId,
          @Param("productId") Long productId);
}
