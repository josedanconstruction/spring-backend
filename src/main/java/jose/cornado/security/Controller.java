package jose.cornado.security;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
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


@RestController(value="SecurityController")
@RequestMapping(Controller.apiMapping)
public class Controller {

	public final static String apiMapping = "/api/security";
	
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;
	@Autowired
	protected AuthenticationManager authenticationManager;
	
	private JWTTokenUtil tokenService;
	
	@PostMapping("/login")
	public  DeferredResult<ResponseEntity<String>> loging(@RequestBody @Valid User user, BindingResult bindingResult){
		
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();

		taskPool.execute(()->{
			try{
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.name, user.password, Collections.emptyList());
				auth.setDetails(user);
				setToken(dr, authenticationManager.authenticate(auth));
				dr.setResult(new ResponseEntity<String>("Ok", HttpStatus.OK));
			}
			catch(BadCredentialsException x){
				dr.setErrorResult(x);
			}
			catch(Exception x){
				x.toString();
			}
		}, AsyncTaskExecutor.TIMEOUT_IMMEDIATE);
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
				if (role.getAuthority().endsWith(u.role))
					success = true;
			}
				
			if (success){
				dr.setResult(tokenService.addAuthentication(u));
			}
			else{
				res = ResponseEntity.badRequest().build();
				dr.setResult(res);
			}
		}
	}

}
