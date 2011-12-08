package com.heroku.javaworker;

/**
 * Java worker
 *
 */
public class JavaWorker 
{
    public static void main( String[] args ) throws InterruptedException
    {

        while(true){

            System.out.println("worker application running...");
        	Thread.sleep(1000);

        }
    }    

}