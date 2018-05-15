package jose.cornado.security;

import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import io.jsonwebtoken.Claims;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REServicesEndPointJWTFilter extends AbstractAuthenticationProcessingFilter{

	private static final Logger logger = LoggerFactory.getLogger(REServicesEndPointJWTFilter.class);
	private final HashSet<String> verbs;
	private final HashSet<String> roles;
	
	REServicesEndPointJWTFilter(String path, HashSet<String> v, HashSet<String> r) {
		super(new AntPathRequestMatcher(path));
		verbs = v;
		roles = r;
	}

	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
		logger.info(String.format("User %s was successfully authorized to %S in path %s", auth.getName(), req.getMethod(), req.getServletPath()));
		SecurityContextHolder.getContext().setAuthentication(auth);
		chain.doFilter(req, res);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest arg0, HttpServletResponse arg1)
			throws AuthenticationException, IOException, ServletException {
		Claims claims;
		String token;
		HttpServletRequest request;
		JWTTokenUtil tokenUtil;

		if (!(arg0 instanceof HttpServletRequest)){
			throw new ServletException("Not an HTTP request");
		}
		request = (HttpServletRequest) arg0;
		if (!verbs.contains(request.getMethod())){
			throw new BadCredentialsException("INVALID method/verb");
		}
		if ((token = request.getHeader("Authorization")) == null){
			throw new BadCredentialsException("Token NOT present");
		}
		tokenUtil = new JWTTokenUtil();
		claims = tokenUtil.getClaims(token);
		if (!roles.contains(claims.get("role").toString().toUpperCase())){
			throw new BadCredentialsException("INVALID role for area/method/verb");
		}

		if (!request.getServletPath().startsWith(String.format("/api/%s", claims.get("area").toString()))){
			throw new BadCredentialsException("Token issued for DIFFRENT area");			
		}
		token = claims.get("role").toString();
		ArrayList<SimpleGrantedAuthority> grantedRole;
		grantedRole = new ArrayList<SimpleGrantedAuthority>();
		grantedRole.add(new SimpleGrantedAuthority(String.format("%s%s", "ROLE_", token.toUpperCase())));		
		return new UsernamePasswordAuthenticationToken(claims.get("user").toString(), null, grantedRole);
	}
	
	public void onAuthenticationFailure(HttpServletRequest arg0, HttpServletResponse arg1, AuthenticationException arg2)
	 			throws IOException, ServletException {
		logger.debug("Failed authorization", arg2);		
	 }
}