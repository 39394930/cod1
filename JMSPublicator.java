package apachetomeejms;

import java.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.io.File;
import java.nio.file.Files;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
public class JMSPublicator {

    // Proprietăți pentru conectarea la broker
    public static Properties getProp() {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
        "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61617");
        return props;
    }
    
    // Citirea fișierului imagine și transformarea în Base64
    public static String encodeImageToBase64(String imagePath) throws IOException {
        byte[] imageBytes = Files.readAllBytes(new File(imagePath).toPath());
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static void main(String[] args) {
        Connection connection = null;
        try {
            InitialContext jndiContext = new InitialContext(getProp());
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.setClientID("durable");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("jms/topic/test");
            MessageProducer producer = session.createProducer(destination);
            String imagePath ="C:\\Users\\Stefania\\Downloads\\Imagine";
            String base64Image = encodeImageToBase64(imagePath);
            TextMessage textMessage = session.createTextMessage(base64Image);
            producer.send(textMessage);
            System.out.println("Imaginea a fost trimisă în format Base64 către topic!");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           
            while (true) {
                System.out.println("Press 'Q' pentru a închide sesiunea.");
                String input = reader.readLine();
                if ("Q".equalsIgnoreCase(input.trim())) {
                    break;
                }
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
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
