package hu.psprog.leaflet.tlp.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Web Security configuration.
 *
 * @author Peter Smith
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String ENDPOINT_LOGS = "/logs";
    private static final String ENDPOINT_V2_LOGS = "/v2/logs";

    private static final String SCOPE_READ_LOGS = "SCOPE_read:logs";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, ENDPOINT_LOGS)
                        .permitAll()
                    .antMatchers(HttpMethod.GET, ENDPOINT_LOGS)
                        .hasAuthority(SCOPE_READ_LOGS)
                    .antMatchers(HttpMethod.POST, ENDPOINT_V2_LOGS)
                        .hasAuthority(SCOPE_READ_LOGS)
                    .and()

                .csrf()
                    .disable()

                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()

                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)

                .build();
    }
}
