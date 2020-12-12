package com.qy.ticket.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.qy.ticket.util.NumberUtil;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/24 下午8:23
 **/
public class FeeDeserialize extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return NumberUtil.multiply100(new BigDecimal(jsonParser.getValueAsString()));
    }
}
