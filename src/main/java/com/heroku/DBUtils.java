package com.heroku;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DBUtils {

	public static void updateDB(AppCloneRequest req) throws Exception{
		Connection connection=null;
		try {
			URI dbUri = new URI(System.getenv("DATABASE_URL"));

			String username = dbUri.getUserInfo().split(":")[0];
			String password = dbUri.getUserInfo().split(":")[1];
			String dbUrl = "jdbc:postgresql://" + dbUri.getHost()
					+ dbUri.getPath();

			connection = DriverManager.getConnection(dbUrl, username, password);
			PreparedStatement pStmt = connection.prepareStatement("update clone_request set status='complete',appName=?,appUrl=?,appgitUrl=? where id=?");
			pStmt.setString(1,req.appName);
			pStmt.setString(2,req.appUrl);
			pStmt.setString(3, req.appGitUrl);
			pStmt.setLong(4,req.id);
			pStmt.execute();
			System.out.println(String.format("[Requested By:%s] - %s : %s",req.emailAddress,"DBUPDT","Updated REquest to Complete"));
			pStmt.close();
		} finally {
			connection.close();
		}
	}
}
