package ie.cct.cbwa.cashowcase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5892895973663439767L;
	
	//constructor
	public BadRequestException(String message) {
		super(message);
	}

}
