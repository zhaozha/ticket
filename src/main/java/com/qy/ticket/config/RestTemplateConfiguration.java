package com.qy.ticket.config;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.List;

/**
 * @author zhaozha
 * @date 2020/1/7 下午12:04
 */
@Configuration
public class RestTemplateConfiguration {
  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    setRestTemplateEncode(restTemplate);
    return restTemplate;
  }

  @Bean
  public RestTemplate wxRefundRestTemplate(
      @Value("${tx.wx.pay.file}") String keyFile, @Value("${tx.wx.pay.pwd}") String keyPassword) {
    return getWxRestTemplate(keyFile, keyPassword);
  }

  private RestTemplate getWxRestTemplate(String keyFile, String keyPassword) {
    InputStream instream = null;
    HttpComponentsClientHttpRequestFactory httpRequestFactory = null;
    try {
      ClassPathResource classPathResource = new ClassPathResource(keyFile);
      // 获取文件流
      instream = classPathResource.getInputStream();
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(instream, keyPassword.toCharArray());
      SSLContext sslContext =
          SSLContexts.custom().loadKeyMaterial(keyStore, keyPassword.toCharArray()).build();
      CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();
      httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(instream);
    }
    RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
    setRestTemplateEncode(restTemplate);
    return restTemplate;
  }

  private static void setRestTemplateEncode(RestTemplate restTemplate) {
    if (null == restTemplate || ObjectUtils.isEmpty(restTemplate.getMessageConverters())) {
      return;
    }

    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    for (int i = 0; i < messageConverters.size(); i++) {
      HttpMessageConverter<?> httpMessageConverter = messageConverters.get(i);
      if (httpMessageConverter.getClass().equals(StringHttpMessageConverter.class)) {
        messageConverters.set(i, new StringHttpMessageConverter(StandardCharsets.UTF_8));
      }
    }
  }
}
