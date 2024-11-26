package com.lyj.securitydomo.config;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class CustomSecurityConfig {

    // UserDetailsService는 사용자 인증 정보를 가져오는 서비스로, Security 설정에 사용
    private final UserDetailsService userDetailsService;

    // SecurityFilterChain을 설정하여 각 요청의 보안 규칙을 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // HttpSecurity를 통해 CSRF 비활성화 및 권한 규칙 설정
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        // 특정 DispatcherType을 허용
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        // 로그인, 회원가입, 특정 경로는 인증 없이 접근 가능
                        .requestMatchers("/login", "/signup","/replies/**", "/user/**", "/", "/all", "/posting/**", "/view/**").permitAll()
                        // POST 요청의 /report/create 경로에 대한 접근 허용 (신고 기능)
                        .requestMatchers(HttpMethod.POST, "/report/create").permitAll()
                        // 관리자 경로 접근 권한 설정 (ADMIN 권한 필요)
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        // 정적 리소스에 대한 접근 허용
                        .requestMatchers("/images/**", "/css/**", "/js/**", "/webjars/**").permitAll()
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated())
                // 폼 로그인 설정
                .formLogin(form -> form
                        .loginPage("/user/login") // 사용자 정의 로그인 페이지 경로 설정
                        .loginProcessingUrl("/loginProcess") // 로그인 처리를 위한 URL
                        .defaultSuccessUrl("/posting/list") // 로그인 성공 후 이동할 페이지 (홈페이지)
                        .failureUrl("/user/login?error=true")  // 로그인 실패 시 URL
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 처리 URL
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 경로
                        .invalidateHttpSession(true) // 세션 무효화
                        .clearAuthentication(true)); // 인증 정보 제거

        // AuthenticationManager를 HttpSecurity 내에서 설정
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());

        return http.build(); // 최종 SecurityFilterChain 반환
    }

    // BCryptPasswordEncoder 빈 설정으로 비밀번호 암호화 사용
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    }
