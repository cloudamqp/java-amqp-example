import static java.nio.charset.StandardCharsets.UTF_8;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

public class Publisher {
  public static void main(String[] args) throws Exception {
    try (Connection connection = Amqp.connect()) {
      publish(connection.createChannel(), "hello", "Hello CloudAMQP!");
    }
  }

  public static void publish(Channel channel, String queue, String message) throws Exception {
    channel.queueDeclare(queue, true, false, false, null);
    channel.confirmSelect();
    channel.basicPublish("", queue, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(UTF_8));
    channel.waitForConfirmsOrDie(5_000);
    System.out.println(" [x] Sent '" + message + "'");
  }
}
