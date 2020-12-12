package com.qy.ticket.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.qy.ticket.util.NumberUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/24 下午8:25
 **/
public class FeeSerialize extends JsonSerializer<Integer> {
    @Override
    public void serialize(Integer integer, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(NumberUtil.divide100(new BigDecimal(integer)));
    }
}