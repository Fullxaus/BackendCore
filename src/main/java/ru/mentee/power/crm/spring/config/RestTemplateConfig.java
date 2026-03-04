package ru.mentee.power.crm.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate() {
    // TODO: при наличии RestTemplateBuilder — setConnectTimeout(5s), setReadTimeout(10s)
    return new RestTemplate();
  }
}
