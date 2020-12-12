package com.qy.ticket.aspect;

import com.qy.ticket.common.CommonResult;
import com.qy.ticket.exception.BusinessException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.qy.ticket.exception.EumException.PARAMETER_VALIDATION_ERROR;


/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/16 上午10:50
 **/
@Order(1)
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity handleBindException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        CommonResult commonResult = new CommonResult(PARAMETER_VALIDATION_ERROR);
        commonResult.setMsg(fieldError.getDefaultMessage());
        return ResponseEntity.ok(commonResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity handleBindException(BusinessException ex) {
        return ResponseEntity.ok(new CommonResult(ex));
    }
}

