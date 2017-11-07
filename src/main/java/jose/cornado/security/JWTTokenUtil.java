package jose.cornado.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static java.util.Collections.emptyList;

class JWTTokenUtil {
	static final long EXPIRATIONTIME = 10000 * 30;
	static final String SECRET = "ThisIsASecret";
	static final String TOKEN_PREFIX = "Bearer";
	static final String HEADER_STRING = "Authorization";
	static String last;
	
	void addAuthentication(HttpServletResponse res, User user) {
		String JWT = Jwts.builder()
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.claim("user", user.name)
				.claim("role", user.role)
				.claim("area", user.role.equalsIgnoreCase("admin") ? "/administrative" : "/client")
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
	}

	Claims getClaims(String token) {
		Claims claims = null;
		try {
			claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();
		} catch (Exception x) {
			x.printStackTrace();
		}
		return claims;
	}
}