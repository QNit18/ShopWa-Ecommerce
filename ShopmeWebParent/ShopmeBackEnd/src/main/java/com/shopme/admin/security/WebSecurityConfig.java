package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new ShopmeUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
               .antMatchers("/users/**", "/settings/**").hasAuthority("Admin")
               .antMatchers("/categories/**", "/brands/**", "/articles/**", "/menus/**").hasAnyAuthority("Admin", "Editor")
               .antMatchers("/products/**").hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
               .antMatchers("/questions/**", "/reviews/**").hasAnyAuthority("Admin", "Assistant")
               .antMatchers("/customers/**", "/shipping/**", "/reports").hasAnyAuthority("Admin", "Salesperson")
               .antMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")


               .anyRequest().authenticated()
               .and()
               .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .permitAll()
               .and().logout().permitAll()
               .and()
                    .rememberMe()
                        .key("AbcDefgHijKlmnOpqrs_1234567890")
                        .tokenValiditySeconds(7 * 24 * 60 * 60 * 2);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Spring Security sẽ bỏ qua và không áp dụng các quy tắc bảo mật lên chúng.
        web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
}
