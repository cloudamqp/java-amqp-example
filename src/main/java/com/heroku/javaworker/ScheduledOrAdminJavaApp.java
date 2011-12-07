package com.heroku.javaworker;

/**
 * Hello world!
 *
 */
public class ScheduledOrAdminJavaApp 
{
    public static void main( String[] args )
    {
        try{

            //initializeApplication

            //performApplicationLogic

        }catch(RuntimeException ex){

            //tryToHandleTheError

            //If error is not expected
            //System.exit(APP_ERROR_CODE);

        }finally{

            //Do any aplication cleanup (closing db connections etc.)

        }
    }    

}