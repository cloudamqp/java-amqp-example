import static java.nio.charset.StandardCharsets.UTF_8;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.util.function.Consumer;

public class Worker {
  public static void main(String[] args) throws Exception {
    Connection connection = Amqp.connect();
    consume(connection.createChannel(), "hello", message -> System.out.println(" [x] Received '" + message + "'"));
    System.out.println(" [*] Waiting for messages");
  }

  public static void consume(Channel channel, String queue, Consumer<String> handler) throws IOException {
    channel.queueDeclare(queue, true, false, false, null);
    channel.basicQos(10);
    channel.basicConsume(queue, false, (consumerTag, delivery) -> {
      handler.accept(new String(delivery.getBody(), UTF_8));
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    }, consumerTag -> {});
  }
}
