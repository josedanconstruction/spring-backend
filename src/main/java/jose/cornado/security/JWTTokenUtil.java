package jose.cornado.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jose.cornado.models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import java.util.Date;


final class JWTTokenUtil {
	private static final Logger logger = LoggerFactory.getLogger(JWTTokenUtil.class);
	static final long EXPIRATIONTIME = 1000 * 60 * 5;
	static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static String last;
	
	final ResponseEntity<String> addAuthentication(User user) {
		Date expiration = new Date(System.currentTimeMillis() + EXPIRATIONTIME);
		String JWT = Jwts.builder()				
				.setExpiration(expiration)
				.claim("user", user.name)
				.claim("role", user.role)
				.claim("area", user.role.equalsIgnoreCase("admin") ? "admin" : "client")
			 	.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
		logger.info(String.format("Token issued for %s, expiration: %s", user.name, expiration));
		return new ResponseEntity<String>(TOKEN_PREFIX + " " + JWT, HttpStatus.OK);		
	}

	final Claims getClaims(String token) {
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
		} catch (Exception x) {
			logger.debug("Internal Exception", x);
			throw x;
		}
		return claims;
	}
}