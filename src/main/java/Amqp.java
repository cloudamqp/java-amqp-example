import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Amqp {
  public static Connection connect() throws Exception {
    String uri = System.getenv().getOrDefault("CLOUDAMQP_URL", "amqp://guest:guest@localhost");
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri(uri);
    return factory.newConnection();
  }
}
