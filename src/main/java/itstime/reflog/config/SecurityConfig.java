package itstime.reflog.config;

import java.util.List;

import itstime.reflog.oauth.handler.OAuthLoginFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.oauth.handler.OAuthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuthLoginSuccessHandler successHandler;
    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
//    private final CustomOAuth2UserService customOAuthService;
    private final MemberRepository memberRepository;


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/test", "/swagger-ui/index.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/v3/api-docs", "/api/v1/todo/**", "/api/v1/learn/**", "/api/v1/plan/**", "/api/v1/weekly-analysis/**","/api/v1/monthly-analysis/**","/api/v1/retrospect/**").permitAll()
                        .anyRequest().authenticated()           // 나머지 URL은 인증 필요
                )
                // .addFilterBefore(new TokenAuthenticationFilter(jwtTokenProvider()), UsernamePasswordAuthenticationFilter.class)
                .formLogin(FormLoginConfigurer::disable)
                // OAuth 로그인 설정
                .oauth2Login(customConfigurer -> customConfigurer
                        .successHandler(successHandler)
                        .failureHandler(oAuthLoginFailureHandler)
//                        .userInfoEndpoint(endpointConfig -> endpointConfig.userService(customOAuthService))
                )
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable);


        return http.build();
    }

}
