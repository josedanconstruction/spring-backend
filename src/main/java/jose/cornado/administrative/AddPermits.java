package jose.cornado.administrative;

import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import jose.cornado.MongoRepo;
import jose.cornado.models.REServiceArea;

final class AddPermits extends ACityTask{

	private static final Logger logger = LoggerFactory.getLogger(AddPermits.class);
	
	private DeferredResult<ResponseEntity<String>> deferredResult;
	
	AddPermits(DeferredResult<ResponseEntity<String>> dr, RestTemplate rc, MongoRepo mt, REServiceArea a) throws Exception{
		super(rc, mt, a);
		deferredResult = dr;
	}
	
	public void run() {
		ArrayList<Document> list;
		HeadersBuilder<?> hb;
		Gson gson;
		String s;
		Document d;
		StringBuilder sb;
		JsonObject job, geometry;
		ResponseEntity<String> response;
		Hashtable<String, JsonObject> geoJson = null;

		for(String p : resLocation.permits){
			try{
				//Boulder site only seems to accept curl as user agent
				hb = RequestEntity.get(new URL(p).toURI()).accept(MediaType.ALL).header("user-agent", "curl/7.43.0"); 
				response = restClient.exchange(hb.build(), String.class);
				if (response.getStatusCodeValue() == 200){
					if (response.hasBody()){
						try(CSVReader rd = new CSVReader(new StringReader(response.getBody()))){ //initial read of the permits file
							Iterable<String[]> it = () -> rd.iterator();
							list = new ArrayList<Document>();
							gson =  new Gson();
							//Grab the geojson location
							if (geoJson == null)
								geoJson= loadGeoJsonLocations(gson);
							//First line discarded
							it.iterator().next();
							//Building the log document to insert in mongo. 
							sb = new StringBuilder(String.format("{\"area\" : \"%s\", \"when\" : \"%s\",\"insert\": [", resLocation.area ,new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()))));
							for(String[] row : it){
								//We get the row from the csv reader, pass it to BoulderCountyCase then serialize this into json.
								job = new JsonParser().parse(gson.toJson(new BoulderCountyCase(row))).getAsJsonObject();
								s = job.get("number").getAsString();
								geometry = geoJson.get(s);
								//If there is geojson attach it to document
								if (geometry != null){
									sb.append(String.format("\"Found geojson->%s\",", s));
									job.add("geometry", geometry);
								}
								else
									sb.append(String.format("\"Missing geojson->%s\",", s));
								d = Document.parse(job.toString());
								//Analyze the permit description to make sure it is a reportable permit.
								if (isReportable(d.getString("description")))
									d.put("reportable", true);
								else
									d.put("reportable", false);
								
								list.add(d);
								//If we hit 1000 cases bulk insert them 
								if (list.size() == 1000){
									repo.bulkInsert(resLocation.area, list);
									list.clear();
								}
							}
							//Make sure no case is left behind
							if (list.size() != 0){
								repo.bulkInsert(resLocation.area, list);
								list.clear();
							}
							//close json array
							sb.setCharAt(sb.length() - 1, ']');
							//close json object
							sb.append('}');
							//build an index for faster lookups
							repo.buildIndex(resLocation.area, "number");
							repo.insert("log", Document.parse(sb.toString()));
							//This is the date that triggers the update
							resLocation.lastModified = System.currentTimeMillis();
//							resLocation.lastModified = new SimpleDateFormat("mm/dd/yyyy").parse("06/01/2017").getTime(); //when reading from local May 2017 file
							repo.addToCityCatalog(resLocation);
							//The report is called master
							repo.insert(resLocation.area, Document.parse("{ reports : true, list : [\"master\"]}"));
							logger.info(String.format("area \"%s\" successfully processed", resLocation.area));
						}
						catch(Exception x){
							logger.debug(String.format("area \"%s\" caused exception:", resLocation.area), x);
							repo.deleteCityFromCatalog(resLocation);
							break;
						}
					}
				}
			}
			catch(Exception x){
				logger.debug(String.format("area \"%s\" caused exception:", resLocation.area), x);
				break;
			}
		}
	}
}