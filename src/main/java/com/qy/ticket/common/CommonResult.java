package com.qy.ticket.common;

import com.qy.ticket.exception.BusinessException;
import com.qy.ticket.exception.EumException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/16 上午9:59
 **/
@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CommonResult implements Serializable {
    private Integer status;
    private String msg;
    private Object data;

    public CommonResult(EumException eumException) {
        this.status = eumException.getStatus();
        this.msg = eumException.getMsg();
        this.data = eumException.getData();
    }

    public CommonResult(BusinessException businessException) {
        this.status = businessException.getStatus();
        this.msg = businessException.getMsg();
        this.data = businessException.getData();
    }
}
