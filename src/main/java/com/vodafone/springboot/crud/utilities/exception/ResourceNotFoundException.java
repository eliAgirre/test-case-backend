package com.vodafone.springboot.crud.utilities.exception;

import java.io.Serial;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception{

	@Serial
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message){
    	super(message);
    }
}
