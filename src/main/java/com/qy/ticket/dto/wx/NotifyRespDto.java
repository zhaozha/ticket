package com.qy.ticket.dto.wx;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author 赵志浩
 * @email 1341777000@qq.com
 * @date 2020/10/22 下午10:50
 **/
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "xml")
public class NotifyRespDto {
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;
    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg;
}

