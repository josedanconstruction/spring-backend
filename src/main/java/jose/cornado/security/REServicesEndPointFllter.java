package jose.cornado.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import io.jsonwebtoken.Claims;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

public class REServicesEndPointFllter extends GenericFilterBean{

	
	private final Hashtable<String, HashSet<String>> verbToPath;
	private JWTTokenUtil tokenService;
	
	REServicesEndPointFllter(Hashtable<String, HashSet<String>> vtp) {
		verbToPath = vtp;
	}
	
	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
		Claims claims;
		String token, path;
		HashSet<String> paths;
		HttpServletRequest request;
		HttpServletResponse response;
		SimpleGrantedAuthority sga;
		ArrayList<SimpleGrantedAuthority> grantedRole;
		UsernamePasswordAuthenticationToken auth;
		if (arg0 instanceof HttpServletRequest){
			request = (HttpServletRequest)arg0;
			response = (HttpServletResponse)arg1;
			token = request.getHeader("Authorization");
			if (token != null){
				paths = verbToPath.get(request.getMethod());
				if (paths != null){
					path =((HttpServletRequest) arg0).getServletPath();
					if (paths.contains(path)){
						if (tokenService == null)
							tokenService = new JWTTokenUtil();
						
						claims = tokenService.getClaims(token);
						token = claims.get("area").toString();
						if (path.startsWith(token)){
							token = claims.get("role").toString();
							grantedRole = new ArrayList<SimpleGrantedAuthority>();
							grantedRole.add(new SimpleGrantedAuthority(String.format("%s%s", "ROLE_", token.toUpperCase())));
							auth =  new UsernamePasswordAuthenticationToken(claims.get("user").toString(), null, Collections.emptyList());
							SecurityContextHolder.getContext().setAuthentication(auth);
							arg2.doFilter(arg0, arg1);
						}
					}
				}
			}
		}
		else
			throw new ServletException("INVALID request");
	}
}