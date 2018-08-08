package jose.cornado;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

public /*abstract*/ class Case {
	
	@Expose
	public Case lastModification;
	@Expose
	public String lastModificationDate;
	@Expose
	public boolean newCase;
	@Expose
	public boolean reportable;
	
	@Expose
	public String number;
	
	@Expose
	public String address;

	@Expose public String assesorId;

	@Expose public String status;

	@Expose public String category;

	public List<String> usesAndscopes;

	public List<String> permitTypes;

	public long totalValue;

	public long subPermitValue;
	
	@Expose public String applied;
	
	@Expose
	public /*DateFormat*/String approved;

	@Expose
	public /*DateFormat*/String issued;

	@Expose
	public /*DateFormat*/String coDate;

	@Expose
	public /*DateFormat*/String completionDate;

	@Expose
	public Integer newUnits;
	
	@Expose
	public Integer reUnits;

	@Expose
	public Integer affordableUnits;

	@Expose
	public Integer newSquareFeet;
	
	@Expose
	public Integer reSquareFeet;

	@Expose
	public String description;

	@Expose
	public String primaryFirst;

	@Expose
	public String primaryLast;

	@Expose
	public String primaryCompany;

	@Expose
	public String contractorFirst;

	@Expose
	public String contractorLast;

	@Expose
	public String contractorCompany;

	@Expose
	public String owner1First;

	@Expose
	public String owner1Last;

	@Expose
	public String owner1Company;

	@Expose
	public String owner2First;

	@Expose
	public String owner2Last;

	@Expose
	public String owner2Company;
	
	@SuppressWarnings("unchecked")
	public void delta(StringBuilder sb, Case c, String ts) throws Exception{
		int top1, top2;
		List<String> list1, list2;
		Object o1, o2;
		String s1, s2;
		ArrayList<String> tmpAL;
		Field fa[] = this.getClass().getFields();
		
		lastModification = new Case();
		lastModification.lastModification = c.lastModification;
		lastModification.lastModificationDate = ts;
		for(Field f : fa){
			if (f.getName().equals("lastModification"))
				continue;
			if (f.getName().equals("newCase")){
				if (f.getBoolean(this) == true){
					f.setBoolean(this, false);
					sb.append(String.format("\"Removed NEW flag from permit: %s\"", number));
				}
			}
			o1 = f.get(this);
			o2 = f.get(c);
			if (o1 instanceof List<?>){
				list1 = (List<String>)o1;
				list2 = (List<String>)o2;
				top1 = list1.size();
				top2 = list2.size();
				tmpAL = new ArrayList<String>();
				for(int i = 0; i < top1; i++){
					s1 = list1.get(i);
					if (i < top2){
						s2 = list2.get(i);
						if (!s1.equalsIgnoreCase(s2)){
							tmpAL.add(s2);
							sb.append(String.format("\"Permit: %s field: %s current: %s previous %s\",", number, f.getName(), s1, s2));
						}
					}
					else{
						tmpAL.add("null");
						sb.append(String.format("\"Permit: %s field: %s current: %s previous %s\",", number, f.getName(), s1, "null"));
					}
					//TODO CHECK top2 > top1
				}
				f.set(lastModification, tmpAL);
			}
			else if (o1 != null && !o1.equals(o2)){
				f.set(lastModification, o2);
				sb.append(String.format("\"Permit: %s field: %s current: %s previous %s\",	", number, f.getName(), o1, o2));
			}
		}
	}
}
