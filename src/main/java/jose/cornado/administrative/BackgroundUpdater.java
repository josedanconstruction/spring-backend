package jose.cornado.administrative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jose.cornado.MongoRepo;
import jose.cornado.models.REServiceArea;

import java.util.HashSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Component
public class BackgroundUpdater implements CommandLineRunner {
	
	@Autowired
	private MongoRepo repo;
	
	@Autowired
	RestTemplate restClient;
	
	@Autowired
	private SimpleAsyncTaskExecutor taskPool;

	private HashSet<String> loadedCities = new HashSet<String>();
    
    public void run(String...args) throws Exception {
    	while(!Thread.currentThread().isInterrupted()){
    		//Get the current cities we are serving data from mongo and launch a background task to 
    		//keep track of updates to the permit files url
    		for(REServiceArea resa : repo.getCityCatalog()){
    			if (!loadedCities.contains(resa.area)){
    				loadedCities.add(resa.area);
    				taskPool.execute(new UpdatePermits(restClient, repo, resa), AsyncTaskExecutor.TIMEOUT_IMMEDIATE);
    			}
    		}
    		Thread.sleep(5000);
    	}
    }
}