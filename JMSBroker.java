package javalin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.broker.BrokerService;

public class JMSBroker {
	public static void initBroker(String ip, String port) throws Exception {
         BrokerService broker = new BrokerService();
         // configure the broker
         broker.addConnector("tcp://"+ip+":"+port);
         broker.start();
 	}

	public static void main(String[] args) {
		//try { initBroker("127.0.0.1", "61617"); } catch(Exception e) {e.printStackTrace();}
		try { initBroker(args[0], args[1]); } catch(Exception e) {e.printStackTrace();}

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
                 System.out.println("Type 'Q' for closing JMS Broker service from ActiveMQ - KahaDB - Apache TomEE Server");
                 try {
		   String input = reader.readLine();
                   if ("Q".equalsIgnoreCase(input.trim())) {
                        break;
		   }
                 } catch (IOException ioe) {}
         	}
	}
}