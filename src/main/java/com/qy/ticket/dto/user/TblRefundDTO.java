package com.qy.ticket.dto.user;

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
public class TblRefundDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  @NotBlank(message = "手机号不能为空")
  private String phoneNum;

  @NotNull(message = "行程不可为空")
  private Long recordId;

  @NotNull(message = "管理员不可为空")
  private Long managerId;

  @NotNull(message = "票数不可为空")
  @Min(value = 0, message = "票数必须大于0")
  private Integer ticketNum;
}
