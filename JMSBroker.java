import jakarta.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSBroker {
    private static final String BROKER_URL = "tcp://localhost:61616"; // Brokerul ActiveMQ
    private static final String TOPIC_NAME = "ImageTopic"; // Numele topicului pentru mesajele de imagine

    public static void startBroker(String brokerURL) {
        // Creăm o conexiune la brokerul ActiveMQ
        try {
            // Creăm o fabrică de conexiuni pentru ActiveMQ
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            
            // Creăm o conexiune la broker
            Connection connection = connectionFactory.createConnection();
            connection.start(); // Pornim conexiunea
            
            // Creăm o sesiune
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            // Creăm topicul pe care publicatorul va publica mesajele
            Topic topic = session.createTopic(TOPIC_NAME);
            
            // Creăm un producer care trimite mesaje pe topic
            MessageProducer producer = session.createProducer(topic);
            
            // Publicăm un mesaj pe topic
            String base64Image = "Base64EncodedImageString"; // Înlocuiește cu un șir Base64 valid
            TextMessage message = session.createTextMessage(base64Image);
            
            // Trimitem mesajul
            producer.send(message);
            
            System.out.println("Mesajul cu imaginea a fost trimis pe topicul " + TOPIC_NAME);
            
            // Închidem sesiunea și conexiunea
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Pornim brokerul și trimitem mesajul
        startBroker(BROKER_URL);
    }
}
