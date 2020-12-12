package com.qy.ticket.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblBill {
  /** 账单编号 */
  @Id private Long id;

  /** 手机号 */
  @Column(name = "phone_num")
  private String phoneNum;

  /** 充值金额 */
  private BigDecimal amount;

  /** 退款金额 */
  @Column(name = "refund_amount")
  private BigDecimal refundAmount;

  /** 时间 */
  private Date time;

  @Column(name = "open_id")
  private String openId;

  /** 0未支付1支付成功2全额退款 */
  private Integer status;

  /** 用户编号 */
  @Column(name = "user_id")
  private Long userId;

  /** 景区编号 */
  @Column(name = "park_id")
  private Long parkId;

  /** 产品编号 */
  @Column(name = "product_id")
  private Long productId;
}
