package jose.cornado.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import jose.cornado.ReactiveMongoRepo;

@RestController(value="ClientController")
@RequestMapping(Controller.apiMapping)
public class Controller {

	public final static String apiMapping = "/client/api"; 
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;
	@Autowired
	private ReactiveMongoRepo mongoRepo;
	
	@GetMapping()
	public  DeferredResult<ResponseEntity<String>> getReport(@RequestParam(name="area", required=true) String area, @RequestParam(name="report", required=false) String report){
		
		DeferredResult<ResponseEntity<String>> dr = new DeferredResult<ResponseEntity<String>>();
		
		if(report != null)
			taskPool.execute(new RestRunnable(area, mongoRepo, dr, RestRunnable.Tasks.REPORT));
		else
			taskPool.execute(new RestRunnable(area, mongoRepo, dr, RestRunnable.Tasks.REPORT_LIST));

		return dr;
	}
}
