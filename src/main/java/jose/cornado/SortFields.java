package jose.cornado;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

public class SortFields{
	@Id
	BigInteger key;
	SortField[] sortFieldArray;
}