package com.sample.board.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sample.board.config.authentication.LoginFailureHandler;
import com.sample.board.config.authentication.LoginSuccessHandler;
import com.sample.board.config.authentication.PrincipalDetailService;
import com.sample.board.config.authentication.UserDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	PrincipalDetailService principalDetailService;
	
	@Autowired
	LoginSuccessHandler loginSuccessHandler;
	
	@Autowired
	LoginFailureHandler loginFailureHandler;
	
	@Autowired
	UserDeniedHandler userDeniedHandler;
	
	@Bean
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
//			.csrf().disable()
			.authorizeRequests()
				.antMatchers("/","/js/**","/css/**","/image/**","/auth/**")
				.permitAll()
				.antMatchers("/admin/**")
				.hasRole("ADMIN")
				.anyRequest()
				.authenticated()
			.and()
				.formLogin()
				.loginPage("/auth/loginForm")
				.loginProcessingUrl("/auth/login")
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)
			.and()
				.exceptionHandling()
				.accessDeniedHandler(userDeniedHandler)
				;
	}
}
