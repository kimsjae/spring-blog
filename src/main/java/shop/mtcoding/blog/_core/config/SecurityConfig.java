package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 컴퍼넌트 스캔
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer ignore() {
        return w -> w.ignoring().requestMatchers("/board/*", "/static/**", "/h2-console/**"); // security에서 막지 말란 것. 접근 가능
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf(c -> c.disable());

        http.authorizeHttpRequests(a -> {
            a.requestMatchers("/user/updateForm", "/board/**").authenticated()
                    .anyRequest().permitAll(); // 이 페이지는 인증이 필요해, 아닌 주소는 모두 허용해줘.

        });

        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/").failureUrl("/loginForm"); // 인증이 필요한 것 이쪽으로 리다이렉션, 시큐리티의 로그인을 쓸 것이다, 성공하면 어디로 가줄까?, 실패하면 어디로 가줄까?
        });
        return  http.build();
    }
}
