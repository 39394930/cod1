package apachetomeejms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageProducerClient_JakartaTomEE {

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
            MessageProducer producer = session.createProducer(destination);

            // Citirea fișierului imagine (exemplu: "image.jpg")
            File imageFile = new File("image.jpg");
            FileInputStream fis = new FileInputStream(imageFile);
            byte[] imageBytes = new byte[(int) imageFile.length()];
            fis.read(imageBytes);
            fis.close();

            // Crearea unui BytesMessage și trimiterea imaginii
            BytesMessage msg = session.createBytesMessage();
            msg.writeBytes(imageBytes);
            msg.setStringProperty("fileName", imageFile.getName()); // Opțional: setăm numele fișierului în proprietăți

            // Citirea mesajelor din consolă (poți trimite alte mesaje text dacă vrei)
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Enter Message to Topic or Press 'Q' for Close this Session");
                String input = reader.readLine();
                if ("Q".equalsIgnoreCase(input.trim())) {
                    break;
                }
                // Dacă utilizatorul nu vrea să trimită o imagine, poate trimite un mesaj text
                if (!input.trim().isEmpty()) {
                    TextMessage textMsg = session.createTextMessage(input);
                    producer.send(textMsg);
                }
            }

            // Trimiterea imaginii
            producer.send(msg);
            System.out.println("Image sent successfully!");

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
