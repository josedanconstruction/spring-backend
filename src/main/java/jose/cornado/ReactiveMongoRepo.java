package jose.cornado;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import jose.cornado.models.REServiceArea;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveMongoRepo {

	@Autowired
	private ReactiveMongoTemplate template;
	
	public Mono<ReportList> getAvailableReports(String collection) {
		Query query2;
		Mono<ReportList> ret = null;
		if (template.collectionExists(collection).block()){
			query2 = new Query();
			query2.addCriteria(Criteria.where("reports").is(true));
			ret = template.findOne(query2, ReportList.class, collection);
		}
		return ret;
	}
	
	public Flux<Case> getMasterReport(String collection, int pageSize, int page){		
		Query query2 = new Query();
		query2.skip(page * pageSize);
		query2.limit(pageSize);
		query2.fields().exclude("_id");
		query2.addCriteria(Criteria.where("reportable").is(true));
		query2.with(new Sort(Sort.Direction.DESC, "totalValue"));
		return template.find(query2, Case.class, collection);
	}
	
	public Mono<List<REServiceArea>> getAvailableCities(){
		return template.findAll(REServiceArea.class, "cities").collectList(); 
	}
	
	public Mono<SortFields> updateSortFields(SortField[] sortFields, String userName, String city)
	{
		SortFields sf = new SortFields();
		try {
			sf.key = new BigInteger(MessageDigest.getInstance("MD5").digest(String.format("%s:%s", userName, city).getBytes()));
			sf.city = city;
			sf.userName = userName;
			sf.sortFieldArray = sortFields;		
		} catch (NoSuchAlgorithmException e) {
		}
		return template.save(sf, "users");

	}
}