package com.qy.ticket.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhaozha
 * @date 2020/1/7 下午1:14
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblBillDTO {
  @NotNull(message = "手机号不能为空")
  private Long userId;

  @NotBlank(message = "手机号不能为空")
  private String phoneNum;

  @NotNull(message = "下单金额不能为空")
  @Min(value = 0, message = "下单金额必须大于0")
  private BigDecimal amount;

  @NotBlank(message = "openid不能为空")
  private String openId;

  @NotNull(message = "票不可为空")
  private List<TblTicketDTO> list;
}
