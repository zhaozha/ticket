package com.qy.ticket.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zhaozha
 * @date 2020/1/8 上午10:58
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblSpecialRefundDTO {
  @NotBlank(message = "手机号不能为空")
  private String phoneNum;

  @NotNull(message = "行程不可为空")
  private Long recordId;

  @NotNull(message = "管理员不可为空")
  private Long managerId;

  @NotNull(message = "退款金额不可为空")
  private BigDecimal refundAmount;

  private Boolean flag;
}
