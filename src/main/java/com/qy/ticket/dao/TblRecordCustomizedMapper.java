package com.qy.ticket.dao;

import com.qy.ticket.dao.mapper.QueryMapper;
import com.qy.ticket.entity.TblRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午5:11
 **/
public interface TblRecordCustomizedMapper extends QueryMapper<TblRecord> {
    @Update("update tbl_record set amount = amount + #{amount},income = income + #{amount}"
            + " ,effective_num = effective_num + #{ticketNum},available_num = available_num + #{ticketNum},total_num = total_num + {ticketNum}"
            + " where id = #{id}")
    int charge2Upd(@Param("id") Long id, @Param("amount") Integer amount, @Param("returnableAmount") Integer returnableAmount, @Param("ticketNum") Integer ticketNum);

    @Update("update tbl_record set available_num = 0,used_num = effective_num," +
            "income= income - #{refundAmount},refundAmount = refundAmount + #{refundAmount}"
            + " where id = #{id}")
    int cancellation2Upd(@Param("id") Long id, @Param("amount") Integer refundAmount);

    @Update("update tbl_record set reason = '指定金额退款'"
            + " where id = #{id}")
    int reason(@Param("id") Long id);

    @Update("<script>" +
            "update tbl_record set available_num = 0,used_num = effective_num"
            + " where  id in " +
            " <foreach collection='ids' item='item' open='(' separator=',' close=')'> " +
            " #{item} </foreach>" +
            "</script>")
    int cancellationAll2Upd(@Param("ids") List<Long> ids);

    @Update("update tbl_record set effective_num = effective_num - #{effectiveNum},available_num = available_num - #{availableNum}," +
            "income= income - #{refundAmount},refundAmount = refundAmount + #{refundAmount}"
            + " where id = #{id}")
    int refund2Upd(@Param("id") Long id, @Param("effectiveNum") Integer effectiveNum, @Param("availableNum") Integer availableNum, @Param("refundAmount") Integer refundAmount);

    @Select("select sum(amount) as amount,sum(refund_amount) as refundAmount,sum(income) as income,sum(effective_num) as effectiveNum"
            + " from tbl_record"
            + " where time >= #{startTime}"
            + " and time <= #{endTime}"
            + " and park_id = #{parkId}"
            + " and product_id =#{productId}")
    TblRecord Sum(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("parkId") Long parkId, @Param("productId") Long productId);

    @Select(
            "select sum(amount) as amount,sum(refund_amount) as refundAmount,sum(income) as income,sum(effective_num) as effectiveNum,DATE_FORMAT( time, '%Y-%m-%d' ) AS time"
                    + " from tbl_record"
                    + " where time >= #{startTime}"
                    + " and time <= #{endTime}"
                    + " and park_id = #{parkId}"
                    + " and product_id =#{productId}"
                    + " GROUP BY"
                    + " DATE_FORMAT( time, '%Y-%m-%d' ) order by DATE_FORMAT( time, '%Y-%m-%d' ) DESC")
    List<TblRecord> Day(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("parkId") Long parkId, @Param("productId") Long productId);

    @Select("select sum(amount) as amount,sum(refund_amount) as refundAmount,sum(income) as income,sum(effective_num) as effectiveNum,DATE_FORMAT( time, '%Y-%m-%d' ) AS time"
            + " from tbl_record"
            + " where time >= #{startTime}"
            + " and time <= #{endTime}"
            + " and park_id = #{parkId}"
            + " and product_id =#{productId}"
            + " GROUP BY "
            + " DATE_FORMAT( time, '%Y-%m' ) order by DATE_FORMAT( time, '%Y-%m' ) DESC")
    List<TblRecord> Month(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("parkId") Long parkId, @Param("productId") Long productId);
}
