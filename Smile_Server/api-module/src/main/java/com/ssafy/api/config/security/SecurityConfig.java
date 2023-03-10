package com.ssafy.api.config.security;

import com.ssafy.api.config.security.jwt.JwtAuthenticationFilter;
import com.ssafy.api.config.security.jwt.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 보안 설정 클래스
 *
 * @author 김정은
 * @author 서재건
 */
@Slf4j
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 해당 uri에 대한 필터 적용 제외
     *
     * @return
     */
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().mvcMatchers(
                "/h2-console/**",
                "/favicon.ico"
        );
    }

    /**
     * security 설정
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("[filterChain] 접근");
        http
                .authorizeRequests()
                // 회원가입, 로그인, 휴대폰 인증, 중복 확인, 예외 처리 허용
                .antMatchers("/api/user/register", "/api/user/login", "/api/user/sns").permitAll()
                .antMatchers( "/api/user/check/**").permitAll()
                .antMatchers("/send-one").permitAll()
                .antMatchers("/exception/**").permitAll()
                .anyRequest().authenticated()
                .and()

                // csrf 해제
                .httpBasic().disable()
                .formLogin().disable()
                .cors().disable()
                .csrf().disable()

                // h2 db 접근 허용
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;


        return http.build();

    }
}
