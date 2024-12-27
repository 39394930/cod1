import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Properties;

public class JMSSubscriber {

    public static Properties getProp(String ip, String port) {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://" + ip + ":" + port);
        return props;
    }

    public static void main(String[] args) {
        Connection connection = null;
        Session session = null;

        try {
            // Configurarea conexiunii la broker-ul JMS
            InitialContext jndiContext = new InitialContext(getProp(args[0], args[1]));
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Crearea topic-ului pentru a primi mesaje
            Destination destination = session.createTopic("jms/topic/test");
            MessageConsumer consumer = session.createConsumer(destination);

            // Pornirea conexiunii JMS
            connection.start();

            // Citirea mesajului care conține imaginea în Base64
            Message message = consumer.receive();  // Blochează până când mesajul este primit

            if (message instanceof TextMessage) {
                String base64Image = ((TextMessage) message).getText();
                System.out.println("Imagine primită în Base64: " + base64Image);

                // Decodificarea string-ului Base64 în byte array
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                // Salvarea imaginii într-un fișier
                try (FileOutputStream fos = new FileOutputStream("imagine_receptionata.jpg")) {
                    fos.write(imageBytes);
                    System.out.println("Imaginea a fost salvată cu succes.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Mesajul primit nu este de tipul TextMessage.");
            }

        } catch (Exception e) {
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

