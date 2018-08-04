package jose.cornado.client;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jose.cornado.Case;
import jose.cornado.ReactiveMongoRepo;
import jose.cornado.ReportList;
import jose.cornado.models.REServiceArea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

final class RestRunnable implements Runnable {

	public enum  Tasks {CITIES, REPORT_LIST, REPORT};
	
	private final Tasks task;
	private final String area;
	private final ReactiveMongoRepo mongoRepo;
	private final DeferredResult<ResponseEntity<?>> deferredResult;

	RestRunnable(String a, ReactiveMongoRepo r, DeferredResult<ResponseEntity<?>> dr, Tasks t){
		deferredResult = dr;
		task = t;
		mongoRepo = r;
		area = a;
	}
	
	public void run() {
		Mono<List<REServiceArea>> cities;
		Mono<List<Case>> report;
		Mono<ReportList> reports;
		switch(task){
		case REPORT_LIST:
			reports = mongoRepo.getAvailableReports(area);
			if (reports != null)
				reports.subscribe(this::acceptReportList, this::accept);
			else
				deferredResult.setResult(new ResponseEntity<String>(String.format("Could NOT find a list of reports for: %s", area), HttpStatus.NOT_FOUND));
			break;
		case REPORT:
			report = mongoRepo.getMasterReport(area).collectList();
			report.subscribe(this::acceptReport, this::accept);
			break;
		case CITIES:
			cities = mongoRepo.getAvailableCities();
			cities.subscribe(this::accepCityList, this::accept);
			break;
		}
	}

	private void acceptReportList(ReportList rl){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create(); 
		deferredResult.setResult(new ResponseEntity<String>(gson.toJson(rl.list), HttpStatus.OK));
	}
	
	private void accept(Throwable t){
		deferredResult.setErrorResult(t);
	}
	
	private void acceptReport(List<Case> report){
		deferredResult.setResult(new ResponseEntity<List<Case>>(report, HttpStatus.OK));
	}
	
	private void accepCityList(List<REServiceArea> list){
		Gson gson = new Gson(); 
		deferredResult.setResult(new ResponseEntity<String>(gson.toJson(list), HttpStatus.OK));
	}
}
