package com.tienda.granatec.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringBootSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailService;
	
	
	//metodo para validar que el usuario es correcto
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		auth.userDetailsService(userDetailService)
				.passwordEncoder(getEncoder());
	}
	
	//metodo para restringir ciertas vistas al usuario
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// el metodo csrf() nos ayuda a que 
		//no se inyecte codigo malicioso a la aplicacion
		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/administrador/**").hasRole("ADMIN")//da permisos al directorio administrador y a todos sus metodos internos 
				.antMatchers("/productos/**").hasRole("ADMIN") //permisos para el directorio producto 
				.and().formLogin().loginPage("/usuario/login") //se carga el logion en la siguiente ruta
				.permitAll().defaultSuccessUrl("/usuario/acceder");//el resto el usuario user tiene permisos
				
	}
	
	@Bean
	public BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
}
