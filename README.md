# Run non-web Java processes on Heroku

Some applications can benefit from splitting logic into multiple components:

1. A web process that is consumed by the end-user
2. One or more non-web processes to perform background and admin tasks.

A non-web process can be either:

1. A long running process called a [Worker](http://devcenter.heroku.com/articles/process-model#mapping_the_unix_process_model_to_web_apps), that is waiting on events (either on a database or from a message queue)
2. A [one-off admin process](http://devcenter.heroku.com/articles/oneoff-admin-ps) which can be invoked manually from the command line or from a service like the [Heroku Scheduler](http://addons.heroku.com/scheduler)


## Prerequisites

* Basic Java knowledge, including an installed version of the JVM and Maven.
* Basic Git knowledge, including an installed version of Git.

## Sample Application

A simple app that demonstrates the one-off and worker process types can be created as two simple Java classes and a build file:

    sampleapp/
      pom.xml
      src/
        main/
          java/
            OneOffProcess.java
            WorkerProcess.java

### src/main/java/OneOffProcess.java

    public class OneOffProcess 
    {
        public static void main(String[] args)
        {
            System.out.println("OneOffProcess executed.");
        }    
    }

### src/main/java/WorkerProcess.java

    public class WorkerProcess 
    {
        public static void main(String[] args)
        {
            while(true) {
            	try { 
            	    Thread.sleep(1000);
                } catch(InterruptedException e) {}
                System.out.println("Worker process woke up");
            }
        }    
    }
    
### pom.xml

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>

      <groupId>org.example</groupId>
      <artifactId>herokujavasample</artifactId>
      <version>1.0-SNAPSHOT</version>

      <build>
        <plugins>
          <plugin>
            <groupId>org.codehaus.mojo</groupId>
              <artifactId>appassembler-maven-plugin</artifactId>
              <version>1.1.1</version>
              <configuration> 
                <assembleDirectory>target</assembleDirectory> 
                <programs>
                    <program>
                        <mainClass>WorkerProcess</mainClass>
                        <name>worker</name>
                    </program>
                    <program>
                        <mainClass>OneOffProcess</mainClass>
                        <name>oneoff</name>
                    </program>
                </programs>
              </configuration>
              <executions>
                  <execution>
                      <phase>package</phase><goals><goal>assemble</goal></goals>
                  </execution>            
              </executions>
          </plugin>
        </plugins>
      </build>  

    </project>

The app assembler plugin generates a convenient launch script for starting your application. A single `pom.xml` can define multiple web, worker or admin processes. 

## Run your Application

To build your application simply run:

    :::term
    $ mvn package

Run the worker with:

    :::term
    $ sh target/bin/worker
    Worker process woke up
    Worker process woke up
    Worker process woke up
    ...

(use `target\bin\worker.bat` on Windows). Run the one-off process with:

    :::term
    $ sh target/bin/oneoff
    OneOffProcess executed.
    
That's it. You are now ready to deploy to Heroku.

# Deploy your Application to Heroku

## Create a Procfile

You declare how you want your application executed in `Procfile` in the project root. Create this file as below:

    :::term
    worker: sh target/bin/java-worker

There is no need to add one-off processes to Procfile because these processes are not managed by Heroku.
    
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

    
### Running your processes on Heroku

#### Worker processes

If your process is a worker you can now start and scale it using a command like this:

    :::term
    $ heroku scale worker=1
    Scaling worker processes... done, now running 1

By scaling your workers to more than one dyno you can have more listeners and thereby consume and process more messages simultaneously. To look at the logs for your worker process, you can use the command:

    :::term
    $ heroku logs --tail
    worker application running...
    
#### Admin processes

If your process is a one-off admin process that you wish to run manually on an as needed basis you can do so with the `heroku run` command:

    :::term
    $ heroku run "sh target/bin/scheduled-java"
    Running admin attached to terminal...
    Admin task run.

#### Scheduled processes

Scheduled processes can be run with the [scheduler add-on](http://addons.heroku.com/scheduler). Start by adding the scheduler add-on to your application:

    :::term
    $ heroku addons:add scheduler
    -----> Adding scheduler to strong-night-1577... done, v4 (free)

Then you can manage your scheduled tasks from the scheduler web console. Open the web console:

    :::term
    $ heroku addons:open scheduler

The web console will allow you to specify the command to run for your scheduled process and the frequency with which you'd like it to run.