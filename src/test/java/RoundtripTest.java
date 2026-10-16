import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class RoundtripTest {
  @Test
  void consumerReceivesPublishedMessage() throws Exception {
    String queue = "roundtrip-" + UUID.randomUUID();
    BlockingQueue<String> received = new ArrayBlockingQueue<>(1);

    try (Connection connection = Amqp.connect()) {
      Channel consumerChannel = connection.createChannel();
      Worker.consume(consumerChannel, queue, received::add);

      Channel publisherChannel = connection.createChannel();
      Publisher.publish(publisherChannel, queue, "Hello CloudAMQP!");

      assertEquals("Hello CloudAMQP!", received.poll(5, TimeUnit.SECONDS));
      consumerChannel.queueDelete(queue);
    }
  }
}
