package com.qy.ticket.controller;

import com.qy.ticket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;

/**
 * @author zhaozha
 * @date 2020/1/7 下午3:36
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class XmlController {
  private final UserService userServiceImpl;

  @PostMapping(value = "/wx/confirm")
  public void wxPayConfirm(HttpServletRequest request, HttpServletResponse response) {
    ServletInputStream input = null;
    BufferedOutputStream out = null;
    try {
      input = request.getInputStream();
      String xmlStr = IOUtils.toString(input, request.getCharacterEncoding());
      String resXml = userServiceImpl.wxPayConfirm(xmlStr);
      response.setContentType("test/xml;charset=UTF-8");
      out = new BufferedOutputStream(response.getOutputStream());
      IOUtils.write(resXml, out);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(input);
      IOUtils.closeQuietly(out);
    }
  }

}
