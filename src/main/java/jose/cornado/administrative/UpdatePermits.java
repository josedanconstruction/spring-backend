package jose.cornado.administrative;

import java.io.StringReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import org.bson.Document;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;

import jose.cornado.Case;
import jose.cornado.MongoRepo;
import jose.cornado.REServiceArea;

final class UpdatePermits extends ACityTask {
	int timeToSleep = 60000;  
	
	UpdatePermits(RestTemplate rc, MongoRepo mt, REServiceArea a) {
		super(rc, mt, a);
	}

	public void run() {
		HeadersBuilder hb;
		Gson gson;
		Case permit;
		Document d;
		StringBuilder sb /*, sb1*/;
		JsonObject job, geometry;
		ResponseEntity<String> response;
		String s, timeStamp = null;
		Hashtable<String, JsonObject> geoJson = null; 
//		FileWriter writer;
		while (!Thread.currentThread().isInterrupted()){
 			try {
				for(String p : resLocation.permits){
					try{
//						writer = new FileWriter("/Users/jose/babushka.json");
//						sb1 = new StringBuilder("[");
						hb = RequestEntity.head(new URL(p).toURI()).accept(MediaType.ALL).header("user-agent", "curl/7.43.0").ifModifiedSince(resLocation.lastModified);
						response = restClient.exchange(hb.build(), String.class);
						if (response.getStatusCode().is2xxSuccessful()){
							hb = RequestEntity.get(new URL(p).toURI()).accept(MediaType.ALL).header("user-agent", "curl/7.43.0").ifModifiedSince(this.resLocation.lastModified);
							response = restClient.exchange(hb.build(), String.class);
							if (response.getStatusCode().is2xxSuccessful() && response.hasBody()){
								//try(CSVReader rd = new CSVReader(new FileReader(new File("/Users/jose/2017_Construction_Permits.june.csv")))){
								try(CSVReader rd = new CSVReader(new StringReader(response.getBody()))){
									Iterable<String[]> it = () -> rd.iterator();
									gson =  new Gson();
									if (geoJson == null){
										geoJson = loadGeoJsonLocations(gson);
										//timeStamp = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()));
										timeStamp = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date(1496275200));
									}
									//First line discarded
									it.iterator().next();
									sb = new StringBuilder(String.format("{\"area\" : \"%s\", \"when\" : \"%s\",\"update\": [", resLocation.area ,new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date(System.currentTimeMillis()))));
									for(String[] row : it){
										permit = new BoulderCountyCase(row);
										
										if (repo.isNew(resLocation.area, permit.number)){
											job = new JsonParser().parse(gson.toJson(permit)).getAsJsonObject();
											s = job.get("number").getAsString();
											//These are new permits
											sb.append(String.format("\"**NEW** case->%s\",", s));
											geometry = geoJson.get(s);
											if (geometry != null)
												job.add("geometry", geometry);
											else
												sb.append(String.format("\"Missing geojson->%s\",", s));
											d = Document.parse(job.toString());
											if (isReportable(d.getString("description")))
												d.put("reportable", true);
											else
												d.put("reportable", false);
											d.append("newCase", true);
											repo.insert(resLocation.area, d);
										}
										else{
											permit.delta(sb, repo.getExisting(resLocation.area, permit.number), timeStamp);
											job = new JsonParser().parse(gson.toJson(permit)).getAsJsonObject();
											d = Document.parse(job.toString());
											repo.updateCase(resLocation.area , d);
										}
//										sb1.append(d.toJson());
//										sb1.append(',');
									}
								}
//								sb1.setCharAt(sb1.length() - 1, ']');
//								writer.write(sb1.toString());
//								writer.flush();
//								writer.close();
								if (sb.length() - 1 != '[')
									sb.setCharAt(sb.length() - 1, ']');
								else
									sb.append("\"NONE\"]");
								sb.append('}');
								repo.insert("log", Document.parse(sb.toString()));
								//sresLocation.lastModified = System.currentTimeMillis();
								resLocation.lastModified = 1496275200;
								repo.updateLastModfied(resLocation);
								geoJson = null;
								timeStamp = null;
							}
						}
						Thread.sleep(timeToSleep);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
 			catch (Exception e) {
			}
		}
	}
}
