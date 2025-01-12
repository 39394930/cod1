package apachetomeejms;

import java.io.FileOutputStream;
import java.io.IOException;
import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageConsumerClient_JakartaTomEE {

    public static Properties getProp(String ip, String port) {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://" + ip + ":" + port);
        return props;
    }

    public static void main(String args[]) {
        Connection connection = null;
        try {
            InitialContext jndiContext = new InitialContext(getProp(args[0], args[1]));
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.setClientID("durable");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("jms/topic/test");
            MessageConsumer consumer = session.createConsumer(destination);

            connection.start();

            while (true) {
                Message msg = consumer.receive();
                if (msg instanceof BytesMessage) {
                    BytesMessage byteMsg = (BytesMessage) msg;
                    byte[] imageBytes = new byte[(int) byteMsg.getBodyLength()];
                    byteMsg.readBytes(imageBytes);

                    // Salvarea imaginii pe disc
                    try (FileOutputStream fos = new FileOutputStream("received_image.jpg")) {
                        fos.write(imageBytes);
                    }

                    System.out.println("Image received and saved!");
                }
            }

        } catch (JMSException | IOException | NamingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
