package com.garage.user_service.core.payload.common;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class ResponseEntityDto {

	private static final String SUCCESSFUL = "success";

	private static final String UNSUCCESSFUL = "error";

	private String status;

	private List<Object> results;

	public ResponseEntityDto() {
		status = SUCCESSFUL;
		results = new ArrayList<>();
	}

	public ResponseEntityDto(String message, boolean successful) {
		this();
		this.status = successful ? SUCCESSFUL : UNSUCCESSFUL;
		addToResults(new Acknowledgement(message));
	}

	public ResponseEntityDto(boolean successful, Object data) {
		this();
		this.status = successful ? SUCCESSFUL : UNSUCCESSFUL;
		addToResults(data);
	}

	public ResponseEntityDto(boolean successful, List<Object> data) {
		this();
		this.status = successful ? SUCCESSFUL : UNSUCCESSFUL;
		addToResults(data);
	}

	protected void addToResults(Object data) {
		if (data != null) {
			if (data instanceof Collection<?>) {
				results.addAll((Collection<?>) data);
			}
			else {
				results.add(data);
			}
		}
	}

}
