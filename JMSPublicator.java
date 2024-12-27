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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Properties;

public class JMSPublicator {

    public static Properties getProp(String ip, String port) {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://" + ip + ":" + port);
        return props;
    }

    public static void main(String args[]) {
        Connection connection = null;
        String imagePath = "calea/catre/imagine.jpg";  // Adaugă punct și virgulă aici
        try {
            // Citirea fișierului imagine și conversia acestuia în Base64
            File file = new File(imagePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] imageBytes = new byte[(int) file.length()];
            fileInputStream.read(imageBytes);
            fileInputStream.close();
            String base64String = Base64.getEncoder().encodeToString(imageBytes);

            // Afisarea stringului Base64 (poți să-l trimiti prin JMS)
            System.out.println("Base64 Encoded String: ");
            System.out.println(base64String);

            // Configurarea conexiunii JMS
            InitialContext jndiContext = new InitialContext(getProp(args[0], args[1]));
            ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.setClientID("durable");
            connection.start();  // Pornește conexiunea JMS
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("jms/topic/test");
            MessageProducer producer = session.createProducer(destination);

            // Crearea unui mesaj cu string-ul Base64
            TextMessage imageMessage = session.createTextMessage();
            imageMessage.setText(base64String);  // Setează string-ul Base64 ca text
            producer.send(imageMessage);  // Trimite mesajul Base64

            // Citirea inputului de la utilizator
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("Enter Message to Topic or Press 'Q' for Close this Session");
                String input = reader.readLine();
                if ("Q".equalsIgnoreCase(input.trim())) {
                    break;
                }
                // Mesajul text trimis de utilizator
                TextMessage userMessage = session.createTextMessage();
                userMessage.setText(input);
                producer.send(userMessage);
            }
        } catch (JMSException | IOException | NamingException e) {
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
