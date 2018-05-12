package jose.cornado.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

final class JWTTokenUtil {
	static final long EXPIRATIONTIME = 10000 * 30;
	static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static String last;
	
	final ResponseEntity<String> addAuthentication(User user) {
		String JWT = Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.claim("user", user.name)
				.claim("role", user.role)
				.claim("area", user.role.equalsIgnoreCase("admin") ? "/administrative" : "/client")
			 	.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
		return new ResponseEntity<String>(headers, HttpStatus.OK);		
	}

	final Claims getClaims(String token) {
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return claims;
	}
}