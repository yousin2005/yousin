package kr.co.yousin.setting;

import kr.co.yousin.login.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

    @Autowired
    LoginService loginService;

    @Autowired
    CustomAuthenticationFailureHandler failureHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                     //   .ignoringRequestMatchers("/admin/makeToken")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰을 쿠키에 저장
                        .ignoringRequestMatchers("/logout")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/code", "/login", "/*.html", "/css/*", "/img/*", "/sub/*.html").permitAll() // 모든 요청을 허용
                        .requestMatchers("/info/**", "/sub/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .usernameParameter("code")
                        //.defaultSuccessUrl("/info/today")
                        .defaultSuccessUrl("/info/yousionInfo")
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .userDetailsService(loginService)
        ;
        return http.build();
    }
}