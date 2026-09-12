package com.naskoni.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class WebAppConfig {

  @Bean
  public ShallowEtagHeaderFilter etagFilter() {
    return new ShallowEtagHeaderFilter();
  }
}