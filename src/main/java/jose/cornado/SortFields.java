package jose.cornado;

import org.springframework.data.annotation.Id;

public class SortFields{
	@Id
	String userName;
	String city;
	SortField[] sortFieldArray;
}