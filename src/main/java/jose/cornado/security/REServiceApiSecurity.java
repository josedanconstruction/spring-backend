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
import org.springframework.web.servlet.config.annotation.CorsRegistry;


@Configuration
@EnableWebSecurity
public class REServiceApiSecurity extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		HashSet<String> set;
		Hashtable<String, HashSet<String>> verbToPath = new Hashtable<String, HashSet<String>>();
		verbToPath.put("GET", new HashSet<String>());
		verbToPath.put("PUT", new HashSet<String>());
		set = verbToPath.get("GET");
		set.addAll(Arrays.asList(new String[]{"/client/api", "/administrative/api/logs"}));
		set = verbToPath.get("PUT");
		set.addAll(Arrays.asList(new String[]{"/administrative/api/add"}));
		http
		.cors().and()
		.csrf().disable() //rest api do not need csrf		
		.authorizeRequests()
		.antMatchers(HttpMethod.POST, "/api/security").permitAll()
		.antMatchers(HttpMethod.GET, "/api/client/*", "/api/admin/*").fullyAuthenticated().and()
//		.antMatchers(HttpMethod.PUT, "/administrative/api/*").fullyAuthenticated()
//		.anyRequest().denyAll().and()		
        /*.addFilterBefore(new REServicesEndPointFllter(verbToPath), 
        		UsernamePasswordAuthenticationFilter.class)*/;
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
		configuration.setAllowedOrigins(Arrays.asList("jose + dan"));
		configuration.setAllowedMethods(Arrays.asList("POST"));
		configurationsMap.put("/api/security", configuration);
		configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("jose + dan"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
		configurationsMap.put("/api/admin", configuration);
		configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("jose + dan"));
		configuration.setAllowedMethods(Arrays.asList("GET"));
		configurationsMap.put("/api/client", configuration);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.setCorsConfigurations(configurationsMap);
		return source;
	}
}
