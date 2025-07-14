package giftproject.config;

import giftproject.member.config.LoginMemberArgumentResolver;
import giftproject.member.repository.MemberRepository;
import giftproject.member.util.BCryptEncoder;
import giftproject.member.util.JwtTokenProvider;
import giftproject.member.util.PasswordEncoder;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final AuthenticationInterceptor authenticationInterceptor;

    public WebConfig(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository,
            AuthenticationInterceptor authenticationInterceptor) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(jwtTokenProvider, memberRepository));
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/members/register", "/api/members/login",
                        "/api/products/**", "/h2-console/**",
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                        "/swagger-resources/**", "/webjars/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptEncoder();
    }
}
