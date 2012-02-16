package com.heroku;

import com.heroku.api.App;


public class AppCloneRequest {
	public AppCloneRequest(String emailAddress, String gitUrl,String status) {
		super();
		this.emailAddress = emailAddress;
		this.gitUrl = gitUrl;
		this.status=status;
	}
	public String emailAddress;
	public String gitUrl;
	public String id;
	public String status;
	public String appName;
	public String appUrl;
	public String appGitUrl;;
}
