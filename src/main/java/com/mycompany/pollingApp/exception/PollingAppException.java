package com.mycompany.pollingApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PollingAppException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6203291161423497262L;

	public PollingAppException(String message) {
        super(message);
    }

    public PollingAppException(String message, Throwable cause) {
        super(message, cause);
    }

}
