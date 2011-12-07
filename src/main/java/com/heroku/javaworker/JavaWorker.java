package com.myexamples;

/**
 * Java worker
 *
 */
public class JavaWorker 
{
    public static void main( String[] args )
    {
        try{

            //initializeApplication

            while(true){

                //getTriggeringEvent

                //performApplicationLogic

            }
        }catch(RuntimeException ex){

            //tryToHandleTheError

            //If error is not expected
            //System.exit(APP_ERROR_CODE);

        }finally{
            //Do any aplication cleanup (closing db connections etc.)
        }
    }    

}