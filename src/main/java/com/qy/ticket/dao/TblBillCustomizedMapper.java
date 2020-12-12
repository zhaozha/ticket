package com.qy.ticket.dao;

import com.qy.ticket.dao.mapper.QueryMapper;
import com.qy.ticket.entity.TblBill;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午5:11
 **/
public interface TblBillCustomizedMapper extends QueryMapper<TblBill> {
    @Update("update tbl_bill set status = 1 "
            + " where id = #{id} and status = 0")
    int change2PayStatus(@Param("id") Long id);

    @Update("update tbl_bill set refund_amount = refund_amount - #{refundAmount} "
            + " where id = #{id}")
    int refund2Upd(@Param("id") Long id, @Param("refundAmount") Integer refundAmount);
}
