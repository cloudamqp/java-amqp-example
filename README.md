# Use CloudAMQP in Java from Heroku

This project illustrates how to use the [Java client AMQP library]() to access [CloudAMQP](http://www.cloudamqp.com) from [Heroku](http://www.heroku.com). 

It consists of one [worker](https://github.com/cloudamqp/java-amqp-example/blob/master/src/main/java/WorkerProcess.java) which listens to a queue and prints the messages to the console (and thus to the Heroku log), and a ["oneoff" process](https://github.com/cloudamqp/java-amqp-example/blob/master/src/main/java/WorkerProcess.java) which enqeues messages to that queue. 

For more information on AMQP and how to use it from Java, see [RabbitMQ's tutorial](http://www.rabbitmq.com/getstarted.html). 

## Usage

Clone this repo, create a heroku application, add the [CloudAMQP addon](http://addons.heroku.com/cloudamqp), push the app to Heroku, start a worker (it's free), run the "one off" process, inspect the log. 

    $ git clone git://github.com/cloudamqp/java-amqp-example.git

    $ heroku create --stack cedar
    Creating growing-spring-2298... done, stack is cedar
    http://growing-spring-2298.herokuapp.com/ | git@heroku.com:growing-spring-2298.git
    Git remote heroku added

    $ heroku addons:add cloudamqp:lemur
    ----> Adding cloudamqp:lemur to growing-spring-2298... done, v5 (free)

    $ git push heroku master
    Counting objects: 388, done.
    Delta compression using up to 4 threads.
    Compressing objects: 100% (148/148), done.
    Writing objects: 100% (388/388), 56.33 KiB, done.
    Total 388 (delta 151), reused 379 (delta 149)

    -----> Heroku receiving push
    -----> Java app detected

    ...build output...

    $ heroku scale worker=1
    Scaling worker processes... done, now running 1

    $ heroku run "sh target/bin/oneoff"
    Running sh target/bin/oneoff attached to terminal... up, run.1
     [x] Sent 'Hello CloudAMQP!'

    $ heroku logs
    2012-03-28T16:59:59+00:00 app[worker.1]:  [*] Waiting for messages
    2012-03-28T17:02:34+00:00 heroku[api]: Scale to worker=1 by carl.hoerberg@gmail.com
    2012-03-28T17:03:05+00:00 heroku[run.1]: State changed from created to starting
    2012-03-28T17:03:06+00:00 app[run.1]: Awaiting client
    2012-03-28T17:03:06+00:00 app[run.1]: Starting process with command `sh target/bin/oneoff`
    2012-03-28T17:03:06+00:00 heroku[run.1]: State changed from starting to up
    2012-03-28T17:03:07+00:00 app[worker.1]:  [x] Received 'Hello CloudAMQP!'
    2012-03-28T17:03:08+00:00 heroku[run.1]: Process exited with status 0
    2012-03-28T17:03:08+00:00 heroku[run.1]: State changed from up to complete

