package jose.cornado;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.web.client.RestTemplate;

import com.mongodb.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@SpringBootApplication
public class REServicesApi {

	public static void main(String[] args) {
		SpringApplication.run(REServicesApi.class, args);
	}
	
	@Bean 
    public MongoTemplate getMongoTemplate() {
        return new MongoTemplate(new MongoClient("localhost"), "REServices");
    }
	
	@Bean 
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(MongoClients.create("mongodb://localhost"), "REServices");
    }
	
	@Bean 
	public SimpleAsyncTaskExecutor getSimpleAsyncTaskExecutor(){
		return new SimpleAsyncTaskExecutor();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}
}
