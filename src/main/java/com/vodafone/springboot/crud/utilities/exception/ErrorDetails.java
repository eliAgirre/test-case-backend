package com.vodafone.springboot.crud.utilities.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDetails {

	@JsonProperty("timestamp")
	private Date timestamp;

	@JsonProperty("message")
	private String message;

	@JsonProperty("details")
	private String details;

}
