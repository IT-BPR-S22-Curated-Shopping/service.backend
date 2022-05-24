package bpr.service.backend.configuration.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        corsConfiguration.applyPermitDefaultValues();

        http.cors().configurationSource(request -> corsConfiguration);
        http.csrf().disable();

        http.authorizeRequests()
                .mvcMatchers("/presentation*").permitAll()
                .mvcMatchers( "/api/customer*/*/*").permitAll()
                .anyRequest()
                .authenticated();

        http.oauth2ResourceServer()
                .jwt();
    }
}