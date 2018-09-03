package jose.cornado.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.result.UpdateResult;

import jose.cornado.Case;
import jose.cornado.ReactiveMongoRepo;
import jose.cornado.ReportList;
import jose.cornado.SortField;
import jose.cornado.SortFields;
import jose.cornado.models.REServiceArea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

final class RestRunnable implements Runnable {

	public enum  Tasks {CITIES, REPORT_LIST, REPORT, FILTERS};
	
	private SortField[] sortFields;
	private final Tasks task;
	private final String area, user;
	private final int pageSize, page;
	private final ReactiveMongoRepo mongoRepo;
	private final DeferredResult<ResponseEntity<?>> deferredResult;
	
	
	RestRunnable(String a, String u, ReactiveMongoRepo r, int ps, int p, DeferredResult<ResponseEntity<?>> dr, Tasks t){
		deferredResult = dr;
		task = t;
		mongoRepo = r;
		area = a;
		user = u;
		pageSize = ps;
		page = p;
	}
	
	public void run() {
		Mono<List<REServiceArea>> cities;
		Mono<List<Case>> report;
		Mono<ReportList> reports;
		Mono<SortFields> filtersResult;
		try{
			switch(task){
			case REPORT_LIST:
				reports = mongoRepo.getAvailableReports(area);
				if (reports != null)
					reports.subscribe(this::acceptReportList, this::accept);
				else
					deferredResult.setResult(new ResponseEntity<String>(String.format("Could NOT find a list of reports for: %s", area), HttpStatus.NOT_FOUND));
				break;
			case REPORT:
				report = mongoRepo.getMasterReport(area, user, pageSize, page).collectList();
				report.subscribe(this::acceptReport, this::accept);
				break;
			case CITIES:
				cities = mongoRepo.getAvailableCities();
				cities.subscribe(this::accepCityList, this::accept);
				break;
			case FILTERS:
				filtersResult = mongoRepo.updateSortFields(sortFields, user, area);
				filtersResult.subscribe(this::acceptFiltersResult, this::accept);
				break;
			}
		}
		catch(Exception x){
			
		}
	}
	
	void setFiltersData(SortField[] sf){
		sortFields = sf;
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
		deferredResult.setResult(new ResponseEntity<List<REServiceArea>>(list, HttpStatus.OK));
	}
	
	private void acceptFiltersResult(SortFields r){
		deferredResult.setResult(new ResponseEntity<String>("success", HttpStatus.OK));
	}
}
