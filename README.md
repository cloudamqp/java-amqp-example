# Run Java worker processes on Heroku

Any business application today has two components to it:

1. A web component that is consumed by the end-user
2. A non-web component to supplement your web component.

The non-web component of your application is called a [Worker](http://devcenter.heroku.com/articles/process-model#mapping_the_unix_process_model_to_web_apps) process in Heroku.  This article is going to talk about getting started on the running a Java worker process in your Heroku environment.

## Prerequisites

* Basic Java knowledge, including an installed version of the JVM and Maven.
* Basic Git knowledge, including an installed version of Git.
* A Java web application. If you don't have one follow the first step to create an example. Otherwise skip that step.

## Components of a Java worker process

A java worker process on Heroku comprises of 4 parts:

1. Your application code that you want executed as a worker
2. Your application code dependencies
3. How to assemble your application with Maven
4. How to invoke your worker using the Procfile

### Types of Worker processes

This article covers how to get started with a simple Java worker process. A worker process can be executed in 3 contexts on Heroku:

1. A long running java application that is waiting on events (either through a database or a message queue)
2. A scheduled java application that is invoked through the [Heroku Scheduler](http://addons.heroku.com/scheduler)
3. A [one time admin process](http://devcenter.heroku.com/articles/oneoff-admin-ps)

Each of these contexts are valid uses of a worker process and depending on your use case your could choose to use one of them for your application.

## Create an application if you don't already have one

To start, we need a simple Java project. You can create this using the mvn:create archetype.

    :::term
    $ mvn archetype:create -DgroupId=com.myexamples -DartifactId=herokujavaworker

This should create the project directories, your "pom.xml" and the associated test directories. This creates folder structure below:

    project
    ¦   pom.xml
    ¦
    +---src
    ¦   +---main
    ¦   ¦   +---java
    ¦   ¦       +---com
    ¦   ¦           +---myexamples
    ¦   ¦                   App.java
    ¦   ¦
    ¦   +---test
    ¦       +---java
    ¦           +---com
    ¦               +---myexamples
    ¦                       AppTest.java


It creates class called App.java that would be the main entry point for your application. You can change/remove/rename this to any specific naming convention that you follow. Here's a quick look at the App.java that maven creates:

    :::java
    package com.myexamples;
    
    /**
     * Hello world!
     *
     */
    public class App 
    {
        public static void main( String[] args )
        {
            System.out.println( "Hello World!" );
        }
    }


## Configuring Maven

You can now open your pom.xml and add any dependencies to your Java application. In addition to your dependencies you also should add the [maven appassembler](http://mojo.codehaus.org/appassembler/appassembler-maven-plugin/) plugin to your pom.xml.

    <build>
      <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>2.3.2</version>
            <configuration>
                <source>${java-version}</source>
                <target>${java-version}</target>
            </configuration>
        </plugin>    
        <!-- The maven app assembler plugin will generate a script that sets up the classpath and runs your project -->
        <plugin>
          <groupId>org.codehaus.mojo</groupId>
    	    <artifactId>appassembler-maven-plugin</artifactId>
    	    <version>1.1.1</version>
    	    <configuration> 
      		  <assembleDirectory>target</assembleDirectory> 
      		  <generateRepository>false</generateRepository>
      		  <extraJvmArguments>-Xmx256m</extraJvmArguments>
      		  <programs>
      			  <program>
      				  <mainClass>com.myexamples.App</mainClass>
      				  <name>app</name>
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

If you have renamed App.java or are using a different class as your main entry point, make sure you change the "mainClass" parameter above to reflect the fully qualified name of that class.

You are now ready to add any additional business logic to your application. 

## Run your Application

To build your application simply run:

    :::term
    $ mvn install

This compiles your java classes and also generates a script called "app.sh" that you can use to run your Java application. To run the applicaiton use the command:

    :::term
    $ sh target/bin/app.sh

That's it. You are now ready to deploy this java application to Heroku.

# Deploy your Application to Heroku

## Create a Procfile

You declare how you want your application executed in `Procfile` in the project root. Create this file with a single line:

    :::term
    worker: sh target/bin/app.sh

## Deploy to Heroku

Commit your changes to Git:

    :::term
    $ git add .
    $ git commit -m "Ready to deploy"

Create the app on the Cedar stack:

    :::term
    $ heroku create --stack cedar
    Creating high-lightning-129... done, stack is cedar
    http://high-lightning-129.herokuapp.com/ | git@heroku.com:high-lightning-129.git
    Git remote heroku added

Deploy your code:

    :::term
    Counting objects: 227, done.
    Delta compression using up to 4 threads.
    Compressing objects: 100% (117/117), done.
    Writing objects: 100% (227/227), 101.06 KiB, done.
    Total 227 (delta 99), reused 220 (delta 98)

    -----> Heroku receiving push
    -----> Java app detected
    -----> Installing Maven 3.0.3..... done
    -----> Installing settings.xml..... done
    -----> executing .maven/bin/mvn -B -Duser.home=/tmp/build_1jems2so86ck4 -s .m2/settings.xml -DskipTests=true clean install
           [INFO] Scanning for projects...
           [INFO]                                                                         
           [INFO] ------------------------------------------------------------------------
           [INFO] Building petclinic 0.1.0.BUILD-SNAPSHOT
           [INFO] ------------------------------------------------------------------------
           ...
           [INFO] ------------------------------------------------------------------------
           [INFO] BUILD SUCCESS
           [INFO] ------------------------------------------------------------------------
           [INFO] Total time: 36.612s
           [INFO] Finished at: Tue Aug 30 04:03:02 UTC 2011
           [INFO] Final Memory: 19M/287M
           [INFO] ------------------------------------------------------------------------
    -----> Discovering process types
           Procfile declares types -> web
    -----> Compiled slug size is 62.7MB
    -----> Launching... done, v5
           http://pure-window-800.herokuapp.com deployed to Heroku


Congratulations! Your  app should now be up and running on Heroku. To check the status of your app, run the command:

    :::term
    $ heroku ps
    
To look at the application logs, run the command:

    :::term
    $ heroku logs --tail

