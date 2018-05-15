package jose.cornado.administrative;


import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.client.RestTemplate;
import jose.cornado.MongoRepo;
import jose.cornado.models.REServiceArea;

@RestController(value="AdministrativeController")
@RequestMapping(Controller.apiMapping)
public class Controller {

	private static final Logger logger = LoggerFactory.getLogger(Controller.class);

	public final static String apiMapping = "/api/admin/city"; 
	@Autowired
	private MongoRepo repo;
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;
	
	@PostMapping("/add")
	public DeferredResult<ResponseEntity<String>> add(final @RequestBody @Valid REServiceArea resa, BindingResult bindingResult){
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();
		
		if (!repo.collectionExists(resa.area))
			try {
				taskPool.execute(new AddPermits(dr, restClient, repo, resa), AsyncTaskExecutor.TIMEOUT_IMMEDIATE);
				dr.setResult(new ResponseEntity<String>(String.format("{ \"message\": \"%s successfully queued, check the logs shortly\"}", resa.area), HttpStatus.OK));

			} catch (Exception x) {
				logger.debug("Internal Exception", x);
				dr.setErrorResult(x);
			}
		else
			dr.setErrorResult(String.format("{\"message\": \"%s was already added\"}", resa.area));
		return dr; 
	}
	
	@GetMapping(value="/logs", produces = {"application/json"})
	public DeferredResult<ResponseEntity<String>> logs(){
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();
		taskPool.execute(new CollectLogs(dr, repo));
		return dr;
	}

}