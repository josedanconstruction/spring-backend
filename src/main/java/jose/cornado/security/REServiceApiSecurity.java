package jose.cornado.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class REServiceApiSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		HashSet<String> set = new HashSet<String>(Arrays.asList("GET", "POST"));
		HashSet<String> roles = new HashSet<String>(Arrays.asList("ADMIN"));
		http
		.cors().and()
		.csrf().disable() //rest api does not need csrf		
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/api/security/*").permitAll()
		.antMatchers(HttpMethod.GET, "/api/client/*", "/api/admin/**").authenticated()
		.antMatchers(HttpMethod.POST, "/api/admin/**").authenticated()
		.anyRequest().denyAll().and()		
        .addFilterBefore(new REServicesEndPointJWTFilter("/api/admin/**", set, roles), 
        		UsernamePasswordAuthenticationFilter.class);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Create a default account
		auth.inMemoryAuthentication().withUser("user").password("clave").roles("client");
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		Hashtable<String, CorsConfiguration> configurationsMap = new Hashtable<String, CorsConfiguration>(); 
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("POST"));
		configuration.setAllowedHeaders(Arrays.asList("cache-control", "Content-Type"));
		configurationsMap.put("/api/security/login", configuration);
		configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
		configuration.setAllowedHeaders(Arrays.asList("cache-control", "Content-Type"));
		configurationsMap.put("/api/admin/**", configuration);
		configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET"));
		configuration.setAllowedHeaders(Arrays.asList("cache-control", "Content-Type"));
		configurationsMap.put("/api/client", configuration);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.setCorsConfigurations(configurationsMap);
		return source;
	}
}
