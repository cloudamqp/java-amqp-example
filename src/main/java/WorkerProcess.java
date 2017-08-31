import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class WorkerProcess {
  private final static String QUEUE_NAME = "hello";

  public static void main(String[] argv) throws Exception {
    String uri = System.getenv("CLOUDAMQP_URL");
    if (uri == null) uri = "amqp://guest:guest@localhost";

    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(uri);
    factory.setConnectionTimeout(30000);
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages");

    QueueingConsumer consumer = new QueueingConsumer(channel);
    channel.basicConsume(QUEUE_NAME, true, consumer);

    while (true) {
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      String message = new String(delivery.getBody());
      System.out.println(" [x] Received '" + message + "'");
    }
  }
}
