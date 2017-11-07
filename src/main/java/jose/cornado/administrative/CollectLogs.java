package jose.cornado.administrative;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import jose.cornado.MongoRepo;

final class CollectLogs implements Runnable {

	private final DeferredResult<ResponseEntity<String>> deferredResult; 
	private final MongoRepo repo;
	
	CollectLogs(DeferredResult<ResponseEntity<String>> dr, MongoRepo mt){
		deferredResult = dr;
		repo = mt;
	}
	public void run() {
		deferredResult.setResult(new ResponseEntity<String>(repo.getLogs(), HttpStatus.OK));
	}
}
