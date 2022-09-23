import com.rabbitmq.client.*;
import java.io.IOException;

public class WorkerProcess {
  private final static String QUEUE_NAME = "hello";

  public static void main(String[] argv) throws Exception {
    String uri = System.getenv("CLOUDAMQP_URL");
    if (uri == null)
      uri = "amqp://guest:guest@localhost";

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(uri);
    factory.setConnectionTimeout(30000);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages");

    DefaultConsumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag,
          Envelope envelope,
          AMQP.BasicProperties properties,
          byte[] body)
          throws IOException {
        String message = new String(body);
        System.out.println(" [x] Received '" + message + "'");
      }
    };

    channel.basicConsume(QUEUE_NAME, true, consumer);
  }
}
