package com.qy.ticket.dao;

import com.qy.ticket.dao.mapper.QueryMapper;
import com.qy.ticket.entity.TblBillChild;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/12/12 下午5:11
 **/
public interface TblBillChildCustomizedMapper extends QueryMapper<TblBillChild> {
    @Update("update tbl_bill_child set status = 1 ,record_id = #{recordId}"
            + " where id = #{id} and status = 0")
    int change2PayStatus(@Param("id") Long id, @Param("recordId") Long recordId);

    @Update("update tbl_bill_child set refund_amount = refund_amount + #{refundAmount} "
            + " where id = #{id}")
    int refund2Upd(@Param("id") Long id, @Param("refundAmount") Integer refundAmount);
}
