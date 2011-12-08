# Run non-web Java application on Heroku

Some applications can benifit from splitting logic into multiple components:

1. A web component that is consumed by the end-user
2. A non-web component or background process to supplement the web component.

The non-web component of your application can be executed in three different contexts within Heroku:

1. A long running application called a [Worker](http://devcenter.heroku.com/articles/process-model#mapping_the_unix_process_model_to_web_apps), that is waiting on events (either on a database or from a message queue)
2. A scheduled Java application that is invoked through the [Heroku Scheduler](http://addons.heroku.com/scheduler)
3. A [one time admin process](http://devcenter.heroku.com/articles/oneoff-admin-ps)


## Prerequisites

* Basic Java knowledge, including an installed version of the JVM and Maven.
* Basic Git knowledge, including an installed version of Git.

## Components of a Java worker process

A non-web Java application worker on Heroku comprises of 3 parts:

1. Application code
2. A Maven build file (`pom.xml`) that defines the dependencies and how to assemble the application
3. A Procfile defining how the process is launched


## Create an application if you don't already have one

Create a simple Java application using mvn archetype:create:

    :::term
    $ mvn archetype:create -DgroupId=com.heroku.javaworker -DartifactId=herokujavaworker

This should create the project directories, your "pom.xml" and the associated test directories. Your project folder structure will look like this:

    │   pom.xml
    │
    └───src
        ├───main
        │   └───java
        │       └───com
        │           └───heroku
        │               └───javaworker
        └───test
        |   └───java
        │       └───com
        │           └───heroku
        │               └───javaworker
        

A class called `App.Java` is also created which serves as the main entry point for your application. 

## Worker processes on Heroku

For your application to run as a "worker" process your  Java class would look like:

    :::java
    package com.heroku.javaworker;
    
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
    
The important thing to note here is that the application doesn't exit. Under normal circumstances worker processes should continue to run.

## Scheduled and one off admin processes on Heroku

For your application to run as a scheduled or one off admin process your  Java class would look like:

    :::java
    package com.heroku.javaworker;
    
    /**
     * Scheduled Java
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

## Configuring Maven


You can now open your `pom.xml` and add any dependencies to your Java application. In addition add the [Maven appassembler](http://mojo.codehaus.org/appassembler/appassembler-Maven-plugin/) plugin to the `pom.xml`:

    <build>
      <plugins>
        <plugin>
          <groupId>org.codehaus.mojo</groupId>
            <artifactId>appassembler-Maven-plugin</artifactId>
            <version>1.1.1</version>
    	    <configuration> 
      		  <assembleDirectory>target</assembleDirectory> 
      		  <programs>
      			  <program>
      				  <mainClass>com.heroku.javaworker.JavaWorker</mainClass>
      				  <name>java-worker</name>
      			  </program>
          		  <program>
      				  <mainClass>com.heroku.javaworker.ScheduledOrAdminJavaApp</mainClass>
      				  <name>scheduled-java</name>
      			  </program>
      		  </programs>
    	    </configuration>
        	<executions>
        		<execution>
        			<phase>package</phase>
        			<goals>
        				<goal>assemble</goal>
        			</goals>
        		</execution>  			
        	</executions>
        </plugin>
      </plugins>
    </build>

The app assembler plugin generates a convenient launch script for starting your application.

Note that the mainClass tag points to the class that launches the application. In the application described above that is `JavaWorker.java`, but it would need to be changed for another application.

Now that the application is ready to be run as a worker any other business logic can be added as long as it is bootstrapped from the main class. 


## Run your Application

To build your application simply run:

    :::term
    $ mvn package

This compiles your Java classes and also generates a script called "app.sh" that you can use to run your Java application. To run the applicaiton use the command:

    :::term
    $ sh target/bin/java-worker.sh
    
If you are a windows users, you can do:

    :::term
    C:\YourProject>target\bin\java-worker.bat

That's it. You are now ready to deploy this Java application to Heroku.

# Deploy your Application to Heroku

## Create a Procfile

You declare how you want your application executed in `Procfile` in the project root. Create this file with a single line:

    :::term
    worker: sh target/bin/java-worker.sh

## Deploy to Heroku

Commit your changes to Git:

    :::term
    $ git init
    $ git add .
    $ git commit -m "Ready to deploy"

Create the app on the Cedar stack:

    :::term
    $ heroku create --stack cedar
    Creating empty-fire-9222... done, stack is cedar
    http://empty-fire-9222.herokuapp.com/ | git@heroku.com:empty-fire-9222.git
    Git remote heroku added

Deploy your code:

    :::term
    $ git push heroku master
    Counting objects: 66, done.
    Delta compression using up to 2 threads.
    Compressing objects: 100% (31/31), done.
    Writing objects: 100% (66/66), 15.74 KiB, done.
    Total 66 (delta 10), reused 30 (delta 9)
    
    -----> Heroku receiving push
    -----> Java app detected
    -----> Installing Maven 3.0.3..... done
    -----> Installing settings.xml..... done
    -----> executing /app/tmp/repo.git/.cache/.Maven/bin/mvn -B -Duser.home=/tmp/build_14lc6nws0m7oc -Dmaven.repo.local=/app/tmp/repo.git/.cache/.m2/repository -s /app/tmp/repo.git/.cache/.m2/settings.xml -DskipTests=true clean install
    [INFO] Scanning for projects...
    [INFO]
    [INFO] ------------------------------------------------------------------------
    [INFO] Building herokujavaworker 1.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    ...
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 3.513s
    [INFO] Finished at: Mon Nov 28 15:44:32 UTC 2011
    [INFO] Final Memory: 12M/490M
    [INFO] ------------------------------------------------------------------------
    -----> Discovering process types
           Procfile declares types -> worker
    -----> Compiled slug size is 12K
    -----> Launching... done, v5
       http://empty-fire-9222.herokuapp.com deployed to Heroku

Congratulations! Your  app should now be up and running on Heroku. To look at the application logs, run the command:

    :::term
    $ heroku logs --tail

### Running your processes on Heroku

#### Worker processes

If your process is a worker you can now start and scale it using a command like this:

    :::term
    $ heroku scale worker=1
    Scaling worker processes... done, now running 1

By scaling your workers to more than one dyno you can have more listeners and thereby consume and process more messages simultaneously.

#### Admin processes

If your process is a one-off admin process that you wish to run manually on an as needed basis you can do so with the `heroku run` command:

    :::term
    $ heroku run admin
    Running admin attached to terminal...

#### Scheduled processes

Scheduled processes can be run with the [scheduler add-on](http://addons.heroku.com/scheduler). Start by adding the scheduler add-on to your application:

    :::term
    $ heroku addons:add scheduler
    -----> Adding scheduler to strong-night-1577... done, v4 (free)

Then you can manage your scheduled tasks from the scheduler web console. Open the web console:

    :::term
    $ heroku addons:open scheduler

The web console will allow you to specify the name of your scheduled process and the frequency with which you'd like it to run.