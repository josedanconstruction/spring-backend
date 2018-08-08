package jose.cornado.security;

import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import jose.cornado.models.User;


@RestController(value="SecurityController")
@RequestMapping(Controller.apiMapping)
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);
	
	public final static String apiMapping = "/api/security";
	
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;
	@Autowired
	protected AuthenticationManager authenticationManager;
	
	private JWTTokenUtil tokenService;
	
	@PostMapping("/login")
	public DeferredResult<ResponseEntity<String>> login(final @RequestBody @Valid User user, BindingResult bindingResult){
		
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();

		if (!bindingResult.hasErrors()){
			taskPool.execute(()->{
				try{
					logger.info(String.format("%s is logging in", user));
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.name, user.password, Collections.emptyList());
					auth.setDetails(user);
					setToken(dr, authenticationManager.authenticate(auth));
					dr.setResult(new ResponseEntity<String>("Ok", HttpStatus.OK));
					logger.info(String.format("%s logged in", user));
				}
				catch(BadCredentialsException x){
					logger.info(String.format("%s failed to log in. Password: %s", user, user.password));
					dr.setErrorResult(x);
				}
				catch(Exception x){
					logger.debug("Internal Exception", x);
					dr.setErrorResult(x);
				}
			}, AsyncTaskExecutor.TIMEOUT_IMMEDIATE);
		}
		else{
			//TODO Make it more general log validation errors
			dr.setResult(new ResponseEntity<String>("Invalid Model", HttpStatus.BAD_REQUEST));
		}
		return dr;
	}
	
	private void setToken(DeferredResult<ResponseEntity<String>> dr, Authentication auth){
		User u;
		ResponseEntity<String> res;
		Collection<? extends GrantedAuthority> roles;
		boolean success = false;

		if (tokenService == null)
			tokenService = new JWTTokenUtil();
		roles = auth.getAuthorities();
		if (!roles.isEmpty()){
			//TODO: CHECK AGAINST ALL ROLES
			u = (User)auth.getDetails();
			for(GrantedAuthority role : roles){
				if (role.getAuthority().toLowerCase().endsWith(u.role.toLowerCase())){
					success = true;
					break;
				}
			}
				
			if (success){
				dr.setResult(tokenService.addAuthentication(u));
			}
			else{
				logger.info(String.format("%s did NOT request a valid role", u));
				res = ResponseEntity.badRequest().build();
				dr.setResult(res);
			}
		}
	}
}
