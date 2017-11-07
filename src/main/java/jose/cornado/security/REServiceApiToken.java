package jose.cornado.security;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.google.gson.Gson;

final class REServiceApiToken extends AbstractAuthenticationProcessingFilter implements AuthenticationFailureHandler{

	private Gson gson;
	private JWTTokenUtil tokenService;
	
	public REServiceApiToken(String url, String verb, AuthenticationManager authManager) {
		super(new AntPathRequestMatcher(url, verb));
		setAuthenticationManager(authManager);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		User u;
		Collection<? extends GrantedAuthority> roles;
		boolean success = false;

		if (tokenService == null)
			tokenService = new JWTTokenUtil();
		roles = auth.getAuthorities();
		if (!roles.isEmpty()){
			//TODO: CHECK AGAINST ALL ROLES
			u = (User)auth.getDetails();
			for(GrantedAuthority role : roles){
				if (role.getAuthority().endsWith(u.role))
					success = true;
			}
				
			if (success)
				tokenService.addAuthentication(res, u);
			else
				res.sendError(HttpServletResponse.SC_FORBIDDEN , "User can NOT access this area");
		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest arg0, HttpServletResponse arg1) throws AuthenticationException, IOException, ServletException {
		UsernamePasswordAuthenticationToken auth;
		if (gson == null)
			gson = new Gson();
		User u = gson.fromJson(new InputStreamReader(arg0.getInputStream()), User.class);
		auth = new UsernamePasswordAuthenticationToken(u.name, u.password, Collections.EMPTY_LIST);
		auth.setDetails(u);
		Authentication a = getAuthenticationManager().authenticate(auth); 
		return a;
	}
	
	public void onAuthenticationFailure(HttpServletRequest arg0, HttpServletResponse arg1, AuthenticationException arg2)
			throws IOException, ServletException {
		PrintWriter writer = arg1.getWriter();
		writer.write(arg2.getMessage());
		writer.flush();
	}
}

final class User {
	public String name;
	public String password;
	public String role;
}