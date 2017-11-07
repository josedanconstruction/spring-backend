package jose.cornado;


import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
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
import org.springframework.data.domain.Sort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

// api params keywords, negativeKeywords associations (logical connectors)


@Repository
public class MongoRepo {

	@Autowired
	private MongoTemplate template;
	
	@PostConstruct
	private void init(){
		if (!template.collectionExists("cities")){
			template.createCollection("cities");
			buildIndex("cities", "area");
			template.createCollection("logs");
			buildIndex("logs", "when");
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
		template.indexOps(c).ensureIndex(new TextIndexDefinitionBuilder().onField(f).named("master").build());
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
	
	public String getLogs(){
		StringBuilder sb = new StringBuilder("[");
		MongoCursor<Document> mc = template.getCollection("log").find().iterator();
		while(mc.hasNext()){
			sb.append(mc.next().toJson());
			sb.append(',');
		}
		sb.setCharAt(sb.length() - 1, ']');
		return sb.toString();
	}	
}