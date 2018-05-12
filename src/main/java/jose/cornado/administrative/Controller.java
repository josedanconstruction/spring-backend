package jose.cornado.administrative;

import java.io.BufferedReader;
import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import jose.cornado.MongoRepo;
import josedanconstruction.models.REServiceArea;

@RestController(value="AdministrativeController")
@RequestMapping(Controller.apiMapping)
public class Controller {

	public final static String rootMapping = "/administrative";
	public final static String apiMapping = "/administrative/api"; 
	@Autowired
	private MongoRepo repo;
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;
	
	@PutMapping(value="/add", consumes = {"application/json"})
	public DeferredResult<ResponseEntity<String>> add(final @RequestBody String json){
		String s;
		REServiceArea resa;
		Gson gson = new Gson();
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();
		
		try(BufferedReader br = new BufferedReader(new StringReader(json))){
			resa = gson.fromJson(br, REServiceArea.class);
			if (!repo.collectionExists(resa.area))
				taskPool.execute(new AddPermits(dr, restClient, repo, resa), AsyncTaskExecutor.TIMEOUT_IMMEDIATE);
			else
				dr.setErrorResult(String.format("area->%s was already added", resa.area));

		}
		catch(Exception x){
			if (x instanceof JsonSyntaxException)
				s = "The configuration file is NOT valid";
			else if (x instanceof JsonIOException)
				s = "The configuration file is CORRUPTED";
			else
				s = x.getClass().getName();
			dr.setErrorResult(s);
		}
		return dr; 
	}
	
	@GetMapping(value="/logs", produces = {"application/json"})
	public DeferredResult<ResponseEntity<String>> logs(){
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();
		taskPool.execute(new CollectLogs(dr, repo));
		return dr;
	}

}