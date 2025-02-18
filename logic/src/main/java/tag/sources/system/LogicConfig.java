package tag.sources.system;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Getter
@Configuration
@PropertySource("classpath:logic.properties")
@ComponentScan(basePackages = "tag.sources")
@EnableJpaRepositories(basePackages = "tag.sources")
@EntityScan("tag.sources")
public class LogicConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> customFilter() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean(name = "applicationJdbcTemplate")
    public JdbcTemplate applicationDataConnection(DataSource systemDataSource) {
        return new JdbcTemplate(systemDataSource);
    }
}
