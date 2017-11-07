package jose.cornado.administrative;

import java.util.ArrayList;
import java.util.Arrays;
import jose.cornado.Case;

final class BoulderCountyCase extends Case {

	BoulderCountyCase(String[] csvRow) {
		number = csvRow[0];
		address = csvRow[1];
		assesorId = csvRow[2];
		status = csvRow[3];
		category = csvRow[4];
		usesAndscopes = Arrays.asList(csvRow[5].split(","));
		permitTypes = Arrays.asList(csvRow[6].split(","));
		totalValue = Long.parseLong(csvRow[7].length() != 0 ? csvRow[7] : "0");
		subPermitValue = Long.parseLong(csvRow[8].length() != 0 ? csvRow[8] : "0");
		try {
			if (csvRow[9].length() != 0)
				applied = /*new SimpleDateFormat(*/csvRow[9];//);
			if (csvRow[10].length() != 0)
				approved = /*new SimpleDateFormat(*/csvRow[10];//);
			if (csvRow[11].length() != 0)
				issued = /*new SimpleDateFormat(*/csvRow[11];//);
			if (csvRow[12].length() != 0)
				coDate = /*new SimpleDateFormat(*/csvRow[12];//);
			if (csvRow[13].length() != 0)
				completionDate = /*new SimpleDateFormat(*/csvRow[13];//);
		} catch (Exception e) {
			//logger.error(String.format("One of the following is not a date \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", csvRow[9], csvRow[10], csvRow[11], csvRow[12], csvRow[13]));
		}
		try {
			newUnits = Integer.parseInt(csvRow[14].length() != 0 ? csvRow[14] : "0");
			reUnits = Integer.parseInt(csvRow[15].length() != 0 ? csvRow[15] : "0");
			affordableUnits = Integer.parseInt(csvRow[16].length() != 0 ? csvRow[16] : "0");
			newSquareFeet = Integer.parseInt(csvRow[17].length() != 0 ? csvRow[17] : "0");
			reSquareFeet = Integer.parseInt(csvRow[18].length() != 0 ? csvRow[18] : "0");
		} catch (Exception e) {
//			logger.error(String.format("One of the following is not an integral number \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"", 
//					csvRow[14], csvRow[15], csvRow[16], csvRow[17], csvRow[18]));
		}
		description = csvRow[19];
		primaryFirst = csvRow[20];
		primaryLast = csvRow[21];
		primaryCompany = csvRow[22];
		contractorFirst = csvRow[23];
		contractorLast = csvRow[24];
		contractorCompany = csvRow[25];
		owner1First = csvRow[26];
		owner1Last = csvRow[27];
		owner1Company = csvRow[28];
		owner2First = csvRow[29];
		owner2Last = csvRow[30];
		owner2Company = csvRow[31];
	}
}

/*
protected DateFormat applied;
protected DateFormat approved;
protected DateFormat issued;
protected DateFormat coDate;
protected DateFormat completionDate;
protected Integer newUnits;
protected Integer reUnits;
protected Integer affordableUnits;
protected Integer newSquareFeet;
protected Integer reSquareFeet;
protected String description;
protected String primaryFirst;
protected String primaryLast;
protected String primaryCompany;
protected String contractorFirst;
protected String contractorLast;
protected String contractorCompany;
protected String owner1First;
protected String owner1Last;
protected String owner1Company;
protected String owner2First;
protected String owner2Last;
protected String owner2Company;
*/