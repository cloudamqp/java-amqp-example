package com.heroku;

public class AppCloneRequest {
	public AppCloneRequest(String emailAddress, String gitUrl) {
		super();
		this.emailAddress = emailAddress;
		this.gitUrl = gitUrl;
	}
	public String emailAddress;
	public String gitUrl;
}
