package tag.sources.system;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tag.sources.services.security.JwtAuthFilter;

@Getter
@Configuration
@PropertySource("classpath:auth.properties")
@ComponentScan(basePackages = "tag.sources")
@EnableJpaRepositories(basePackages = "tag.sources")
@EntityScan("tag.sources")
public class AuthConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${password.salt}")
    private String salt;

    @Value("${jwt.expired.millis}")
    private Long expiredMillis;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> customFilter() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
