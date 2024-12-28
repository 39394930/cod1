import jakarta.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.io.FileOutputStream;
import java.io.IOException;

public class JMSSubscriber {

    protected static final String url = "tcp://localhost:61617";

    public static void main(String[] args) {
        String topicName = "jms/topic/test";  // Valoare implicită
        Context jndiContext = null;
        TopicConnectionFactory topicConnectionFactory = null;
        TopicConnection topicConnection = null;
        TopicSession topicSession = null;
        Topic topic = null;
        TopicSubscriber topicSubscriber = null;

        // Verifică dacă argumentele sunt suficiente
        if (args.length > 0) {
            topicName = args[0]; // Folosește primul argument pentru numele topicului
        }
        
        System.out.println("Topic name = " + topicName);

        try {
            // Setează proprietățile pentru JNDI
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, url);

            jndiContext = new InitialContext(props);
            topicConnectionFactory = (TopicConnectionFactory) jndiContext.lookup("ConnectionFactory");
            topicConnection = topicConnectionFactory.createTopicConnection();
            topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = topicSession.createTopic(topicName);

            topicSubscriber = topicSession.createSubscriber(topic);

            // Crearea unui MessageListener care primește mesajele
            topicSubscriber.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof BytesMessage) {
                        try {
                            // Extrage mesajul BytesMessage
                            BytesMessage bytesMessage = (BytesMessage) message;
                            byte[] imageBytes = new byte[(int) bytesMessage.getBodyLength()];
                            bytesMessage.readBytes(imageBytes);

                            // Salvează imaginea pe disc
                            try (FileOutputStream fileOutputStream = new FileOutputStream("received_image.jpg")) {
                                fileOutputStream.write(imageBytes);
                            }
                            System.out.println("Imaginea a fost salvată.");
                        } catch (JMSException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            topicConnection.start();

            // Așteaptă mesaje și permite închiderea programului
            System.out.println("Aștept mesaje...");
            System.in.read(); // Așteaptă apăsarea unei taste pentru a opri programul

        } catch (NamingException | JMSException | IOException e) {
            e.printStackTrace();
        } finally {
            // Închide conexiunea
            try {
                if (topicConnection != null) {
                    topicConnection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
class TextListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
			TextMessage msg = null;
			
			try {
				if(message instanceof TextMessage) {
					msg = (TextMessage)message;
					System.out.println("Received message = "+msg.getText());
				} else {
					System.out.println("Binary messsage!");
				}
			} catch(JMSException jmse) {
				jmse.printStackTrace();
			} catch(Throwable t) {
				t.printStackTrace();
			}
	}

}