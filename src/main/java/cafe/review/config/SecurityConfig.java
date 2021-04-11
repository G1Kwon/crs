package cafe.review.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //기본적으로 csrf 시큐리티 기능이 활성화 되어있다.
        //타사이트에서 나의 사이트로 잘못된 요청을 보내는 것을 막게 하기 위한 csrf 토큰 발행
        //아래 예외처리를 해서 url 들에 대해 인증을 하지 않아도 사용이 가능하지만
        //폼데이터의 csrf 토큰은 무조건 있어야 한다.
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email", "check-email-token",
                        "/email-login", "/check-email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
