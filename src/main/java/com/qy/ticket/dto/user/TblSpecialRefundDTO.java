package com.qy.ticket.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qy.ticket.common.FeeDeserialize;
import com.qy.ticket.common.FeeSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhaozha
 * @date 2020/1/8 上午10:58
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TblSpecialRefundDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  @NotBlank(message = "手机号不能为空")
  private String phoneNum;

  @NotNull(message = "行程不可为空")
  private Long recordId;

  @NotNull(message = "管理员不可为空")
  private Long managerId;

  @NotNull(message = "退款金额不可为空")
  @Min(value = 0, message = "退款金额必须大于0")
  @JsonSerialize(using = FeeSerialize.class)
  @JsonDeserialize(using = FeeDeserialize.class)
  private Integer refundAmount;
}
