package com.example.config;

import com.example.common.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.AuthorizeVO;
import com.example.filter.JsonLoginFilter;
import com.example.filter.JwtAuthenticationFilter;
import com.example.filter.RequestLogFilter;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Configuration
public class SecurityConfig {

    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    RequestLogFilter requestLogFilter;

    @Resource
    JwtUtils utils;

    @Resource
    AccountService service;

    @Resource
    PasswordEncoder encoder;

    /**
     * 认证管理器（交给 Spring 生成）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 认证提供器，用于用户名 + 密码验证
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);   // 你的 AccountService 实现了 UserDetailsService
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    /**
     * 安全过滤链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authenticationManager) throws Exception {

        // ⭐ 核心：使用你自己的 JSON 登录过滤器，而不是 Spring 默认表单登录
        JsonLoginFilter jsonLoginFilter = new JsonLoginFilter(authenticationManager);
        jsonLoginFilter.setAuthenticationSuccessHandler(this::handleAuthenticationSuccess);
        jsonLoginFilter.setAuthenticationFailureHandler(this::handleAuthenticationFailure);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/translate/**").permitAll()
                        .anyRequest().hasAnyRole(Const.ROLE_DEFAULT)
                )

                .authenticationProvider(authenticationProvider())

                // ⭐ 添加自定义 JSON 登录过滤器（替代 UsernamePasswordAuthenticationFilter）
                .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)

                // ⭐ 按正确顺序挂载自定义过滤器
                .addFilterBefore(requestLogFilter, JsonLoginFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, JsonLoginFilter.class)

                .exceptionHandling(conf -> conf
                        .accessDeniedHandler(this::handleAccessDenied)
                        .authenticationEntryPoint(this::handleUnauthorized)
                )

                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )

                .build();
    }

    /*==================  下方全是你原本的处理逻辑（我未改动）  ==================*/

    private void handleAuthenticationSuccess(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();

        User user = (User) authentication.getPrincipal();
        Account account = service.findAccountByNameOrEmail(user.getUsername());
        String jwt = utils.createJwt(user, account.getUsername(), account.getId());

        if (jwt == null) {
            writer.write(RestBean.forbidden("登录验证频繁，请稍后再试").asJsonString());
        } else {
            AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, o -> o.setToken(jwt));
            vo.setExpire(utils.expireTime());
            writer.write(RestBean.success(vo).asJsonString());
        }
    }

    private void handleAuthenticationFailure(HttpServletRequest request,
                                             HttpServletResponse response,
                                             AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    private void handleAccessDenied(HttpServletRequest request,
                                    HttpServletResponse response,
                                    AccessDeniedException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.forbidden(exception.getMessage()).asJsonString());
    }

    private void handleUnauthorized(HttpServletRequest request,
                                    HttpServletResponse response,
                                    AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    private void onLogoutSuccess(HttpServletRequest request,
                                 HttpServletResponse response,
                                 Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (utils.invalidateJwt(authorization)) {
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }
        writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
    }
}