package com.naskoni.library.config;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  @Bean
  public ApplicationSecurity applicationSecurity() {
    return new ApplicationSecurity();
  }

  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    PasswordEncoder sha256PasswordEncoder =
        new PasswordEncoder() {
          @Override
          public String encode(CharSequence rawPassword) {
            return Hashing.sha256().hashString(rawPassword, StandardCharsets.UTF_8).toString();
          }

          @Override
          public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encodedPassword.equals(
                Hashing.sha256().hashString(rawPassword, StandardCharsets.UTF_8).toString());
          }
        };

    @Autowired private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf()
          .disable()
          .authorizeRequests()
          .antMatchers("/api/**")
          .authenticated()
          .and()
          .httpBasic()
          .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService).passwordEncoder(sha256PasswordEncoder);
    }
  }
}
