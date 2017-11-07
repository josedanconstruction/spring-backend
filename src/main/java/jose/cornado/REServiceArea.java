package jose.cornado;

import java.util.List;

public class REServiceArea{
	public String area;
	public List<String> permits;
	public List<String> geo;
	public long lastModified;
	
	public void setArea(String a){
		area = a;
	}
	public String getArea(){
		return area;
	}

	public void setPertmits(List<String> p){
		permits = p;
	}
	public List<String> getPermits(){
		return permits;
	}
	
	public void setGeo(List<String> g){
		geo = g;
	}
	public List<String> getGeo(){
		return geo;
	}

	public void setLastModified(long lm){
		lastModified = lm;
	}
	public long getLastModified(){
		return lastModified;
	}
}
