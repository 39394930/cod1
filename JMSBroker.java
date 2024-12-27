import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.IOException;
import java.io.InputStreamReader;

public class JMSBroker {

    public static Properties getProp(String ip, String port) {
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
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
            
            // Înlocuim TextMessage cu BytesMessage pentru mesajele binare
            BytesMessage msg = session.createBytesMessage();
            
            // Mesajul binar: de exemplu, un simplu array de byte-uri
            String messageContent = "Hello, This is a binary message!";
            byte[] byteArray = messageContent.getBytes();
            
            // Setăm byte-urile în mesajul binar
            msg.writeBytes(byteArray);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Enter Message to Topic or Press 'Q' for Close this Session");
                String input = reader.readLine();
                if ("Q".equalsIgnoreCase(input.trim())) {
                    break;
                }
                // Scriem inputul utilizatorului ca mesaj binar
                byte[] inputBytes = input.getBytes();
                msg.clearBody();
                msg.writeBytes(inputBytes);
                
                // Trimitem mesajul binar
                producer.send(msg);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NamingException e) {
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
