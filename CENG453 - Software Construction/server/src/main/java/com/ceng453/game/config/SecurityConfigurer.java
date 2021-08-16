package com.ceng453.game.config;

import com.ceng453.game.service.UsersDetailsService;
import com.ceng453.game.util.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    /**
     * Injection of UsersDetailsService
     */
    private final UsersDetailsService usersDetailsService;

    /**
     * Injection of JwtRequestFilter
     */
    private final JwtRequestFilter jwtRequestFilter;

    /**
     * This method creates password encoder bean
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * This method overrides configure method of WebSecurityConfigurerAdapter with AuthenticationManagerBuilder parameter
     * @param auth AuthenticationManagerBuilder
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * This method overrides configure method of WebSecurityConfigurerAdapter with HttpSecurity parameter
     * @param http HttpSecurity
     * @throws Exception when an exception caught
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/player/register").permitAll()
                .antMatchers("/api/player/login").permitAll()
                .antMatchers("/api/player/forgetPassword").permitAll()
                .antMatchers("/api/resetPassword/changePassword").permitAll()
                .antMatchers("/v2/api-docs", "/swagger-resources/**",
                        "/swagger-ui.html**",
                        "/webjars/**").permitAll()
                .anyRequest()
                .fullyAuthenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .csrf().disable();
    }

    /**
     * This method creates DaoAuthenticationProvider bean
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(usersDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
}
