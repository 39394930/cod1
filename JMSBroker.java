import jakarta.jms.*;
import jakarta.naming.Context;
import jakarta.naming.InitialContext;
import jakarta.naming.NamingException;
import java.util.Properties;

public class JMSBroker {
    private static final String BROKER_URL = "tcp://localhost:61616";  // Adresa brokerului (ActiveMQ sau al tău personalizat)
    private static final String TOPIC_NAME = "ImageTopic";  // Numele topicului pentru mesajele de imagine

    public static void startBroker(String brokerURL) throws JMSException {
        // Setăm configurările pentru broker
        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, brokerURL);
            
            // Creăm conexiunea și sesiunea JMS
            Context context = new InitialContext(props);
            ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Creăm topicul pe care publicatorul va publica mesajele
            Topic topic = session.createTopic(TOPIC_NAME);
            
            // Creăm un producer care trimite mesaje pe topic
            MessageProducer producer = session.createProducer(topic);
            connection.start();
            
            System.out.println("Brokerul a început și este gata să primească și să trimită mesaje.");
            
            // Publicarea unui mesaj pe topic
            // De exemplu, trimitem un mesaj Base64 (reprezentarea imaginii)
            String base64Image = "Base64EncodedImageString";  // Înlocuiește cu un șir Base64 valid al unei imagini
            TextMessage message = session.createTextMessage(base64Image);
            producer.send(message);

            System.out.println("Mesajul cu imaginea a fost trimis.");
            
            // Închidem resursele
            session.close();
            connection.close();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Pornim brokerul
            startBroker(BROKER_URL);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
