package jose.cornado.administrative;

import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jose.cornado.MongoRepo;
import josedanconstruction.models.PermitUrl;
import josedanconstruction.models.REServiceArea;

abstract class ACityTask  implements Runnable{

	protected final RestTemplate restClient;
	protected final REServiceArea resLocation;
	protected final MongoRepo repo;

	public ACityTask( RestTemplate rc, MongoRepo mt, REServiceArea a) {
		resLocation = a;
		restClient = rc;
		repo = mt;
	}

	protected Hashtable<String, JsonObject> loadGeoJsonLocations(Gson gson) throws Exception {
		HeadersBuilder hb;
		ResponseEntity<String> response;
		JsonObject property, geometry, feature, geoJson;
		String number;
		Hashtable<String, JsonObject> ret = new Hashtable<String, JsonObject>();
		for(PermitUrl g : resLocation.geo){
			hb = RequestEntity.get(new URL(g.getUrl()).toURI()).accept(MediaType.APPLICATION_JSON).header("user-agent", "curl/7.43.0");
			response = restClient.exchange(hb.build(), String.class);
			if (response.getStatusCodeValue() == 200){
				if (response.hasBody()){
					// TODO this could be a json array. Add verification. Log unexpected response status.
					geoJson = (JsonObject) new JsonParser().parse(response.getBody());
					for (JsonElement jeb : geoJson.get("features").getAsJsonArray()){
						feature = jeb.getAsJsonObject(); 
						property = feature.get("properties").getAsJsonObject();
						number = property.get("CASE_NUMBE").getAsString();
						if (number.startsWith("PMT")){
							geometry = feature.get("geometry").getAsJsonObject();
							ret.put(number, geometry); 
						}
					}
				}
			}
		}
		return ret;
	}

	protected boolean isReportable(String desc){
		boolean ret = false;
		String pattern = 
		"\\b([Bb][Ee][Dd]\\w*|[Bb][Aa][Tt][Hh]\\w*|[Ff][Ii][Nn][Ii][Ss][Hh]\\w*|[Dd][Rr][Yy][Ww][Aa][Ll][Ll]\\w*|[Ww][Aa][Ll][Ll]\\w*|[Pp][Oo][Rr][Cc][Hh]\\w*|[Rr][Ee][Mm][Oo][Dd][Ee][Ll]\\w*|[Bb][Aa][Ss][Ee][Mm][Ee][Nn][Tt]|[Cc][Oo][Nn][Vv][Ee][Rr][Tt][Ee][Dd]|[Ee][Xx][Tt][Ee][Rr][Ii][Oo][Rr]|[Ff][Ee][Nn][Cc][Ee]|[Hh][Oo][Uu][Ss][Ee]|[Ii][Nn][Tt][Ee][Rr][Ii][Oo][Rr]|[Kk][Ii][Tt][Cc][Hh][Ee][Nn]|[Mm][Aa][Ss][Tt][Ee][Rr]|[Oo][Ff][Ff][Ii][Cc][Ee]|[Rr][Ee][Bb][Uu][Ii][Ll][Tt]|[Rr][Oo][Uu][Gg][Hh]\\w*|[Ss][Uu][Ii][Tt][Ee])\\b";
		try{
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(desc);
			ret = m.find();
		}
		catch(Exception x){
			x.printStackTrace();
		}
		return ret;
	}
	
	abstract public void run();

}