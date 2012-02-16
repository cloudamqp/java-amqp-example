package com.heroku;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.google.gson.Gson;
import com.heroku.api.App;

public class ShareAppWorker {
	private static JedisPoolFactory poolFactory = new JedisPoolFactory();
    
	public static void main(String[] args) throws InterruptedException {
      
		
        JedisPool pool = poolFactory.getPool();
        Jedis jedis = pool.getResource();
        System.out.println("");
        while(true) {
            String appToClone = jedis.lpop("queue");  
           if(appToClone!=null){
            	AppCloneRequest cloneReq = new Gson().fromJson(appToClone, AppCloneRequest.class);
            	System.out.println(String.format("Received app to clone: Id:%s,Owner Emai:%s,Git URL:%s",cloneReq.id,cloneReq.emailAddress,cloneReq.gitUrl));
            	HerokuAppSharingHelper helper = new HerokuAppSharingHelper(cloneReq.emailAddress,cloneReq.gitUrl);
            	try {
					App clonedApp = helper.cloneApp();
			        List<String> fields = new ArrayList<String>();
			        fields.add("email");
			        fields.add("appUrl");
			        fields.add("appGitUrl");
			        fields.add("id");
			        List<String> request = jedis.hmget(cloneReq.id,"id","email","appUrl","appGitUrl","status");
			        Map<String,String> updtReq = new HashMap<String,String>();
			        updtReq.put("id", request.get(0));
			        updtReq.put("email",request.get(1));
			        updtReq.put("appName",clonedApp.getName());
			        updtReq.put("appUrl",clonedApp.getWebUrl());
			        updtReq.put("appGitUrl", clonedApp.getGitUrl());
				      jedis.hmset(request.get(0), updtReq);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            Thread.sleep(1000);
        }
        
        
    }
}
