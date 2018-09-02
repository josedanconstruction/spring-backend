package jose.cornado.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import jose.cornado.ReactiveMongoRepo;
import jose.cornado.SortField;

@RestController(value="ClientController")
@RequestMapping(Controller.apiMapping)
public class Controller {

	public final static String apiMapping = "/api/client"; 
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;
	@Autowired
	private ReactiveMongoRepo mongoRepo;
	
	@GetMapping("city")
	public  DeferredResult<ResponseEntity<?>> getReport(@RequestParam(name="area", required=true) String area,  @RequestParam(name="report", required=true) String report, @RequestParam(name="pageSize", required=true) int pageSize, @RequestParam(name="page", required=true) int page){
		DeferredResult<ResponseEntity<?>> dr = new DeferredResult<ResponseEntity<?>>();
		
		if(report != null)
			taskPool.execute(new RestRunnable(area, mongoRepo, pageSize, page, dr, RestRunnable.Tasks.REPORT));
		else
			taskPool.execute(new RestRunnable(area, mongoRepo, pageSize, page, dr, RestRunnable.Tasks.REPORT_LIST));

		return dr;
	}
	
	@GetMapping()
	public  DeferredResult<ResponseEntity<?>> getCities(){		
		DeferredResult<ResponseEntity<?>> dr = new DeferredResult<ResponseEntity<?>>();
		taskPool.execute(new RestRunnable(null, mongoRepo, 0, 0, dr, RestRunnable.Tasks.CITIES));
		return dr;
	}
	
	@PutMapping("filters")
	public  DeferredResult<ResponseEntity<?>> updateUserQuery(Authentication auth, @RequestParam(name="area", required=true) String area, @RequestBody(required=true)SortField[] sortFields){
		DeferredResult<ResponseEntity<?>> dr = new DeferredResult<ResponseEntity<?>>();
		RestRunnable rr = new RestRunnable(area, mongoRepo, 0, 0, dr, RestRunnable.Tasks.FILTERS);
		rr.setFiltersData(auth.getName(), sortFields);
		taskPool.execute(rr);
		return dr;
	}
}
