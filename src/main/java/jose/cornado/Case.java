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
	final public void setNumber(String s){
		number = s;
	}
	final public String getNumber(){
		return number;
	}
	
	@Expose
	public String address;
	final public void setAddress(String s){
		address = s;
	}
	final public String getAddress(){
		return address;
	}

	@Expose public String assesorId;
	final public void setAssesorId(String s){
		assesorId = s;
	}
	final public String getAssesorId(){
		return assesorId;
	}

	@Expose public String status;
	final public void setStatus(String s){
		status = s;
	}
	final public String getStatus(){
		return status;
	}

	@Expose public String category;
	final public void setCategory(String s){
		category = s;
	}
	final public String getCategory(){
		return category;
	}

	public List<String> usesAndscopes;
	final public void setUsesAndscopes(List<String> s){
		usesAndscopes = s;
	}
	final public List<String> getUsesAndscopes(){
		return usesAndscopes;
	}

	public List<String> permitTypes;
	final public void setPermitTypes(List<String> s){
		permitTypes = s;
	}
	final public List<String> getPermitTypes(){
		return permitTypes;
	}

	public long totalValue;
	final public void setTotalValue(long l){
		totalValue = l;
	}
	final public long getTotalValue(){
		return totalValue;
	}

	public long subPermitValue;
	final public void setSubPermitValue(long l){
		subPermitValue = l;
	}
	final public long getSubPermitValue(){
		return subPermitValue;
	}
	
	@Expose public String applied;
	final public void setApplied(String s){
		try {
			applied = s;//new SimpleDateFormat(s);
		} catch (Exception e) {
		}
	}
	final public String getApplied(){
		return applied.toString();
	}
	
	@Expose
	public /*DateFormat*/String approved;
	final public void setApproved(String s){
		try {
			approved =  s;//new SimpleDateFormat(s);
		} catch (Exception e) {
		}
	}
	final public String getApproved(){
		return approved.toString();
	}

	@Expose
	public /*DateFormat*/String issued;
	final public void setIssued(String s){
		try {
			issued =  s;//new SimpleDateFormat(s);
		} catch (Exception e) {
		}
	}
	final public String getIssued(){
		return approved.toString();
	}

	@Expose
	public /*DateFormat*/String coDate;
	final public void setCoDate(String s){
		try {
			coDate = s;//new SimpleDateFormat(s);
		} catch (Exception e) {
		}
	}
	final public String getCoDate(){
		return coDate.toString();
	}

	@Expose
	public /*DateFormat*/String completionDate;
	final public void setCompletionDate(String s){
		try {
			completionDate = s;//new SimpleDateFormat(s);
		} catch (Exception e) {
		}
	}
	final public String getCompletionDate(){
		return completionDate.toString();
	}

	@Expose
	public Integer newUnits;
	final public void setNewUnits(String s){
		try {
			newUnits = Integer.parseInt(s);
		} catch (Exception e) {
		}
	}
	final public String getNewUnits(){
		return newUnits.toString();
	}
	
	@Expose
	public Integer reUnits;
	final public void setReUnits(String s){
		try {
			reUnits = Integer.parseInt(s);
		} catch (Exception e) {
		}
	}
	final public String getReUnits(){
		return reUnits.toString();
	}

	@Expose
	public Integer affordableUnits;
	final public void setAffordableUnits(String s){
		try {
			affordableUnits = Integer.parseInt(s);
		} catch (Exception e) {
		}
	}
	final public String getAffordableUnits(){
		return affordableUnits.toString();
	}

	@Expose
	public Integer newSquareFeet;
	final public void setNewSquareFeet(String s){
		try {
			newSquareFeet = Integer.parseInt(s);
		} catch (Exception e) {
		}
	}
	final public String getNewSquareFeet(){
		return newSquareFeet.toString();
	}
	
	@Expose
	public Integer reSquareFeet;
	final public void setReSquareFeet(String s){
		try {
			reSquareFeet = Integer.parseInt(s);
		} catch (Exception e) {
		}
	}
	final public String getReSquareFeet(){
		return reSquareFeet.toString();
	}

	@Expose
	public String description;
	final public void setDescription(String s){
		description = s;
	}
	final public String getDescription(){
		return description;
	}

	@Expose
	public String primaryFirst;
	final public void setPrimaryFirst(String s){
		primaryFirst = s;
	}
	final public String getPrimaryFirst(){
		return primaryFirst;
	}

	@Expose
	public String primaryLast;
	final public void setPrimaryLast(String s){
		primaryLast = s;
	}
	final public String getPrimaryLast(){
		return primaryLast;
	}

	@Expose
	public String primaryCompany;
	final public void setPrimaryCompany(String s){
		primaryCompany = s;
	}
	final public String getPrimaryCompany(){
		return primaryCompany;
	}

	@Expose
	public String contractorFirst;
	final public void setContractorFirst(String s){
		contractorFirst = s;
	}
	final public String getContractorFirst(){
		return contractorFirst;
	}

	@Expose
	public String contractorLast;
	final public void setContractorLast(String s){
		contractorLast = s;
	}
	final public String getContractorLast(){
		return contractorLast;
	}

	@Expose
	public String contractorCompany;
	final public void setContractorCompany(String s){
		contractorCompany = s;
	}
	final public String getContractorCompany(){
		return contractorCompany;
	}

	@Expose
	public String owner1First;
	final public void setOwner1First(String s){
		owner1First = s;
	}
	final public String getOwner1First(){
		return owner1First;
	}

	@Expose
	public String owner1Last;
	final public void setOwner1Last(String s){
		owner1Last = s;
	}
	final public String getOwner1Last(){
		return owner1Last;
	}

	@Expose
	public String owner1Company;
	final public void setOwner1Company(String s){
		owner1Company = s;
	}
	final public String getOwner1Company(){
		return owner1Company;
	}

	@Expose
	public String owner2First;
	final public void setOwner2First(String s){
		owner2First = s;
	}
	final public String getOwner2First(){
		return owner2First;
	}

	@Expose
	public String owner2Last;
	final public void setOwner2Last(String s){
		owner2Last = s;
	}
	final public String getOwner2Last(){
		return owner2Last;
	}

	@Expose
	public String owner2Company;
	final public void setOwner2Company(String s){
		owner2Company = s;
	}
	final public String getOwner2Company(){
		return owner2Company;
	}
	
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
