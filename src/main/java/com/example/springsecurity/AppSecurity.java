package com.example.springsecurity;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class AppSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource datasource;
	

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication().dataSource(datasource)
				.usersByUsernameQuery("select username, password from user where username=?")
				.authoritiesByUsernameQuery("select username,user_type from user where username=?");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("dashboard-service/home/getallpolicy").permitAll()
				.antMatchers("dashboard-service/home/alloffers").permitAll()
				.antMatchers("dashboard-service/home/allpromotions").permitAll()
				.antMatchers("dashboard-service/home/offerbyid/**").permitAll()
				.antMatchers("dashboard-service/home/addpolicy").hasAuthority("APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/policyOwned").hasAnyAuthority("ADMIN,APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/allPolicyOwned").hasAnyAuthority("ADMIN,APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/verifyuser").hasAuthority("CLIENT")
				.antMatchers("dashboard-service/home/register").hasAuthority("CLIENT")
				.antMatchers("dashboard-service/home/updateuser").hasAuthority("CLIENT")
				.antMatchers("dashboard-service/home/createoffer").hasAuthority("APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/deleteoffer/**").hasAuthority("APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/createfeedback").hasAuthority("CLIENT")
				.antMatchers("dashboard-service/home/allfeedback").hasAuthority("APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/promotionbyid/*").hasAuthority("APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/createpromotion").hasAnyAuthority("APLICATION_OWNER")
				.antMatchers("dashboard-service/home/updatepromotion").hasAuthority("APPLICATION_OWNER")
				.antMatchers("dashboard-service/home/deletepromotion/*").hasAuthority("APPLICATION_OWNER").and()
				.formLogin().defaultSuccessUrl("dashboard-service", true).and().logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).and().exceptionHandling()
				.accessDeniedPage(null);
	}
}

