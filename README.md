# Use CloudAMQP in Java from Heroku

    $ git clone git://github.com/cloudamqp/java-amqp-example.git
    $ heroku create --stack cedar
    $ git push heroku master
    $ heroku ps:scale worker=1
    $ heroku run "target/bin/oneoff"
    $ heroku logs

