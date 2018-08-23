package jose.cornado;

import java.util.List;
import javax.annotation.PostConstruct;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition.TextIndexDefinitionBuilder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import jose.cornado.administrative.CityLogHeader;
import jose.cornado.models.REServiceArea;

// api params keywords, negativeKeywords associations (logical connectors)


@Repository
public class MongoRepo {

	@Autowired
	private MongoTemplate template;
	
	@PostConstruct
	private void init(){
		if (!template.collectionExists("cities")){
			template.createCollection("cities");
			template.indexOps("cities").ensureIndex(new TextIndexDefinitionBuilder().onField("area").named("master").build());
			buildIndex("cities", "area");
			template.createCollection("logs");
			buildIndex("logs", "when");
			template.createCollection("users");
			template.indexOps("users").ensureIndex(new TextIndexDefinitionBuilder().onFields("userName", "city").named("userCity").build());			
		}		
	}
	
	public boolean collectionExists(String s){
		return template.collectionExists(s);
	}
	
	public void bulkInsert(String c, List<Document> l){
		template.insert(l, c);
	}
	
	public void insert(String c, Document d){
		template.insert(d, c);
	}
	
	public void buildIndex(String c, String f){		
		
	}
	
	public void addToCityCatalog(REServiceArea resa){
		template.insert(resa, "cities");
	}
	
	public void deleteCityFromCatalog(REServiceArea resa){
		template.remove(resa, "cities");
	}
	
	public List<REServiceArea> getCityCatalog(){
		return template.findAll(REServiceArea.class, "cities");
	}
	
	public boolean isNew(String collection, String number){
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("number").is(number));
		return !template.exists(query2, collection);
	}
	
	public Case getExisting(String collection, String number){
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("number").is(number));
		return template.findOne(query2, Case.class, collection);
	}
	
	public void updateCase(String collection, Document doc){
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("number").is(doc.get("number")));
		//TODO check deleteresult
		template.remove(query2, collection);
		template.insert(doc, collection);
	}
	
	public void updateLastModfied(REServiceArea resa){
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("area").is(resa.area));
		Update update = new Update();
		update.set("lastModified", resa.lastModified);
		template.upsert(query2, update, "cities");
	}
	
	public List<CityLogHeader> getLogs(){
		Query filter = new Query();

		filter.fields().include("area");
		filter.fields().include("when");
		filter.fields().exclude("_id");
		return template.find(filter, CityLogHeader.class, "log");
	}
	
}

