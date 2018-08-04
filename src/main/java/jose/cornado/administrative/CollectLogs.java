package jose.cornado.administrative;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import jose.cornado.MongoRepo;

final class CollectLogs implements Runnable {

	private final DeferredResult<ResponseEntity<List<CityLogHeader>>> deferredResult; 
	private final MongoRepo repo;
	
	CollectLogs(DeferredResult<ResponseEntity<List<CityLogHeader>>> dr, MongoRepo mt){
		deferredResult = dr;
		repo = mt;
	}
	public void run() {
		deferredResult.setResult(new ResponseEntity<List<CityLogHeader>>(repo.getLogs(),  HttpStatus.OK));
	}
}
