package com.qy.ticket.exception;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/16 上午9:59
 **/
public enum EumException {
    // 全局异常 3xxx
    NOT_LOGIN(30001, "用户未登录或登录过期", null),
    TOOL_ERROR(30002, "工具类使用错误", null),
    SERVICE_ERROR(30003, "服务异常", null),
    PERMISSION_ERROR(30004, "入参有误", null),
    SYSTEM_BUSY(30005, "系统繁忙", null),
    QUIET_LOGIN_ERROR(30006, "静默登录失败", null),
    FILE_UPLOAD_ERROR(30007, "文件上传失败", null),
    PARAMETER_VALIDATION_ERROR(30008, "参数有误", null),
    NOT_AGENT_AUTH(30009, "权限不足", null),
    // 登录异常 21xxx
    WX_LOGIN_CODE_ERROR(21001, "code错误", null),
    // 行程异常 22xxx
    RECORD_NOT_EXIST(22001, "无记录", null),
    // 订单异常 23xxx
    ORDER_INFO_ERROR(22001, "下单入参异常", null),
    UNIFIEDORDER_ERROR(22002, "统一下单失败,请稍后再试", null),
    ORDER_ERROR(22003, "订单异常", null),
    ORDER_REFUND_NUM_ERROR(22004, "超过可退票数", null),
    ORDER_REFUND_AMOUNT_ERROR(22005, "退款金额有误", null),
    PART_REFUND_ERROR(22006, "部分退款成功,请联系管理员处理", null),
    REFUND_ERROR(22007, "退款异常", null);
    private Integer status;
    private String msg;
    private Object data;

    EumException(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
