package com.heroku;

import java.io.File;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import com.google.gson.Gson;
import com.heroku.api.App;

public class ShareAppWorker {
	private static JedisPoolFactory poolFactory = new JedisPoolFactory();
    
	public static void main(String[] args) throws InterruptedException {
        java.net.URL url = ClassLoader.getSystemResource("known_hosts");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>known_hosts="+url);
        File knownHostsFile = new File(ClassLoader.getSystemResource("known_hosts").getFile());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>known_hosts file="+knownHostsFile);
        JedisPool pool = poolFactory.getPool();
        Jedis jedis = pool.getResource();
        while(true) {
            String appToClone = jedis.lpop("queue");  
           if(appToClone!=null){
            	AppCloneRequest request = new Gson().fromJson(appToClone, AppCloneRequest.class);
            	System.out.println(String.format("Received app to clone: Owner Emai:%s,Git URL:%s",request.emailAddress,request.gitUrl));
            	HerokuAppSharingHelper helper = new HerokuAppSharingHelper(request.emailAddress,request.gitUrl);
            	try {
					App clonedApp = helper.cloneApp();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            Thread.sleep(200);
        }
        
        
    }
}
