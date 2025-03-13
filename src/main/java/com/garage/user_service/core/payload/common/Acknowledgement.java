package com.garage.user_service.core.payload.common;

import java.util.HashMap;

public class Acknowledgement extends HashMap<String, Object> {

	public Acknowledgement(String message) {
		put("message", message);
	}

	public Acknowledgement(String message, String id) {
		put("message", message);
		put("id", id);
	}

}
