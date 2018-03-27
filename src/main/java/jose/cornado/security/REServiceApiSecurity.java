package jose.cornado.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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
		.csrf().disable() //rest api do not need csrf
		.authorizeRequests().antMatchers("/", "/api/**").permitAll()
		.anyRequest().authenticated().antMatchers(HttpMethod.GET, "/client/api", "/administrative/api/*").authenticated()
		.antMatchers(HttpMethod.PUT, "/administrative/api/*").authenticated()
		.anyRequest().denyAll().and()
        .addFilterBefore(new REServiceApiToken("/login", "POST", authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new REServicesEndPointFllter(verbToPath),
        		UsernamePasswordAuthenticationFilter.class);
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		// Create a default account
		auth.inMemoryAuthentication().withUser("user").password("clave").roles("client");
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
	}
}
