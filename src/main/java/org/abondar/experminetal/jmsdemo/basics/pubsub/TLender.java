package org.abondar.experminetal.jmsdemo.basics.pubsub;


import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


public class TLender {
    private TopicConnection connection = null;
    private TopicSession session = null;
    private Topic topic = null;

    public TLender(String connFactory, String topicName) {
        try {
            Properties env = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("tbl.properties");
            env.load(is);

            Context context = new InitialContext(env);
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup(connFactory);

            connection = factory.createTopicConnection();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = (Topic) context.lookup(topicName);
            connection.start();

        } catch (JMSException | NamingException | IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private void publishRate(double newRate) throws JMSException {
        BytesMessage msg = session.createBytesMessage();
        msg.writeDouble(newRate);

        TopicPublisher publisher = session.createPublisher(topic);
        publisher.publish(msg);
    }

    private void exit() throws JMSException {
        connection.close();
        System.exit(0);
    }


    public static void main(String[] args) throws Exception {
        String connFactory = null;
        String topicName = null;
        if (args.length == 2) {
            connFactory = args[0];
            topicName = args[1];
        } else {
            System.out.println("Invalid argument. Usage: ");
            System.out.println("java TLender factory topic");
            System.exit(0);
        }

        TLender lender = new TLender(connFactory, topicName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("TLender Application Started");
        System.out.println("Press enter to quit");
        System.out.println("Enter: Rate");
        System.out.println("\ne.g 6.8");

        while (true) {
            System.out.print("> ");
            String rate = reader.readLine();
            if (rate == null || rate.trim().length() <= 0) {
                lender.exit();
            }

            double newRate = Double.valueOf(rate);

            lender.publishRate(newRate);
        }
    }
}


