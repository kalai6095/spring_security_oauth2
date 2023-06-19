package com.alainelkhoury.resourceserver.config;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(request -> {
                    request.mvcMatchers("/foo").hasRole("ADMIN")
                            .anyRequest()
                            .authenticated();
                })
                //.oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken)
                .oauth2ResourceServer(oauth2-> oauth2.opaqueToken().withObjectPostProcessor(getObjectPostProcessor()));
    }
    
    private ObjectPostProcessor<BearerTokenAuthenticationFilter> getObjectPostProcessor() {
        return new ObjectPostProcessor<BearerTokenAuthenticationFilter>() {
            @Override
            public <O extends BearerTokenAuthenticationFilter> O postProcess(O filter) {
                filter.setAuthenticationFailureHandler((request, response, exception) -> {
                    System.out.println("Authentication failed (and is being handled in a custom way)");
                    BearerTokenAuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
                    delegate.commence(request, response, exception);
                });
                return filter;
            }
        };
    }
}
