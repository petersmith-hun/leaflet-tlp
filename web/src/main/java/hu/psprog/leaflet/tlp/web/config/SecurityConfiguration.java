package hu.psprog.leaflet.tlp.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(HttpMethod.POST, ENDPOINT_LOGS)
                            .permitAll()
                        .requestMatchers(HttpMethod.GET, ENDPOINT_LOGS)
                            .hasAuthority(SCOPE_READ_LOGS)
                        .requestMatchers(HttpMethod.POST, ENDPOINT_V2_LOGS)
                            .hasAuthority(SCOPE_READ_LOGS))

                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt(jwtConfigurer -> {}))

                .build();
    }
}
