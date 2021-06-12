package ie.cct.cbwa.cashowcase.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2043091615513070689L;
	
	public UnauthorizedException(String message) {
		super(message);
		
	}

}
