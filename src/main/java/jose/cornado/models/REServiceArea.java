package jose.cornado.models;

import java.util.List;

import javax.validation.constraints.NotNull;

public class REServiceArea{
	@NotNull
	public String area;
	public List<String> permits;
	public List<String> geo;	
	public long lastModified;		
}
