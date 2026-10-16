# Use CloudAMQP from Java

A minimal example of publishing and consuming messages with the
[RabbitMQ Java client](https://github.com/rabbitmq/rabbitmq-java-client),
against [CloudAMQP](https://www.cloudamqp.com) or any AMQP 0-9-1 broker such as
[LavinMQ](https://lavinmq.com) or RabbitMQ.

- [`Publisher.java`](src/main/java/Publisher.java) publishes a persistent message with publisher confirms.
- [`Worker.java`](src/main/java/Worker.java) consumes with manual acknowledgements and a prefetch of 10.
- [`Amqp.java`](src/main/java/Amqp.java) connects using the `CLOUDAMQP_URL` environment variable.

## Usage

Tool versions are in `mise.toml`; install them with [mise](https://mise.jdx.dev),
or use Java 21+ and Maven 3.

```sh
export CLOUDAMQP_URL="amqps://user:password@host/vhost"
mvn compile exec:java -Dexec.mainClass=Worker      # in one terminal
mvn compile exec:java -Dexec.mainClass=Publisher   # in another
```

Without `CLOUDAMQP_URL` it connects to `amqp://guest:guest@localhost`.

## Tests

`mvn test` publishes and consumes a message through a real broker on
localhost. CI runs it against LavinMQ.
