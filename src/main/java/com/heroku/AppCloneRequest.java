package com.heroku;


public class AppCloneRequest {
	public AppCloneRequest(String emailAddress, String gitUrl,String status) {
		super();
		this.emailAddress = emailAddress;
		this.gitUrl = gitUrl;
		this.status=status;
	}
	public String emailAddress;
	public String gitUrl;
	public long id;
	public String status;
}
