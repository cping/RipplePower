package org.ripple.power.ui.errors;

import java.net.URI;

public class ErrorResult {

	private ErrorStatus status;

	private String id;

	private URI uri;

	public ErrorResult() {
	}

	public ErrorResult(ErrorStatus s) {
		this.status = s;
	}

	public ErrorStatus getErrorStatus() {
		return status;
	}

	public void setErrorStatus(ErrorStatus s) {
		this.status = s;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
}
