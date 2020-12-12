package com.qy.ticket.exception;

import lombok.Data;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/16 上午10:00
 **/
@Data
public class BusinessException extends RuntimeException {
    private Integer status;
    private String msg;
    private Object data;

    public BusinessException(EumException eumException) {
        super(eumException.getMsg());
        this.status = eumException.getStatus();
        this.msg = eumException.getMsg();
        this.data = eumException.getData();
    }
}