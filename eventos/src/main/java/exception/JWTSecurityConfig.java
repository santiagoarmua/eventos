package exception;

import entity.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import security.entity.*;
import utils.JWTConstants;

import java.util.Collections;

public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String OPTIONS_PATTERN = "/**";
    private static final String REGEX_SEPARATOR = ",";

    private final JWTAuthenticationProvider authenticationProvider;
    private final JWTAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomCorsConfiguration customCorsConfiguration;

    @Autowired
    public JWTSecurityConfig(JWTAuthenticationProvider authenticationProvider,
                             JWTAuthenticationEntryPoint authenticationEntryPoint,
                             CustomCorsConfiguration customCorsConfiguration) {
        this.authenticationProvider = authenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.customCorsConfiguration = customCorsConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    @Bean
    public JWTAuthenticationTokenFilter authenticationTokenFilter() {
        JWTAuthenticationTokenFilter filter = new JWTAuthenticationTokenFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(new JWTSuccessHandler());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().configurationSource(createCorsConfigurationSource())
                .and()
                .authorizeRequests()
                .antMatchers(JWTConstants.PATH_LOGIN, "/content").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new JWTAuthenticationTokenFilter("/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint((request, response, e) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                            new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name(), "token-not-empty").toString()
                    );
                });
    }

    private CorsConfigurationSource createCorsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        Arrays.stream(customCorsConfiguration.getOrigins().split(REGEX_SEPARATOR)).forEach(config::addAllowedOrigin);
        Arrays.stream(customCorsConfiguration.getMethods().split(REGEX_SEPARATOR)).forEach(config::addAllowedMethod);
        Arrays.stream(customCorsConfiguration.getHeaders().split(REGEX_SEPARATOR)).forEach(config::addAllowedHeader);
        source.registerCorsConfiguration(OPTIONS_PATTERN, config);
        return source;
    }
}
