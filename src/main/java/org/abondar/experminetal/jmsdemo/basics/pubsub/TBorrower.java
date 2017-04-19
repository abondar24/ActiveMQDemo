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

public class TBorrower implements MessageListener {

    private TopicConnection connection = null;
    private TopicSession session = null;
    private Topic topic = null;
    private double currentRate;

    public TBorrower(String connFactory, String topicName, String rate) {
        try {
            Properties env = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("tbl.properties");
            env.load(is);

            Context context = new InitialContext(env);
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup(connFactory);

            connection = factory.createTopicConnection();
            session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = (Topic) context.lookup(topicName);

            TopicSubscriber subscriber = session.createSubscriber(topic);
            subscriber.setMessageListener(this);
            connection.start();

            System.out.println("Waiting for loan requests");
        } catch (JMSException | NamingException | IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            BytesMessage msg = (BytesMessage) message;
            double newRate = msg.readDouble();

            if ((currentRate - newRate) >= 1.0) {
                System.out.println("New rate = " + newRate + " - Consider refinancing loan");
            } else {
                System.out.println("New rate = " + newRate + " - Keep existing loan");
            }

            System.out.println("\nWaiting for rate updates...");

        } catch (JMSException jmse) {
            System.err.println(jmse.getMessage());
            System.exit(1);
        }
    }

    private void exit() throws JMSException {
        connection.close();
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        String connFactory = null;
        String topicName = null;
        String rate = null;
        if (args.length == 3) {
            connFactory = args[0];
            topicName = args[1];
            rate = args[2];
        } else {
            System.out.println("Invalid argument. Usage: ");
            System.out.println("java TLender factory topic");
            System.exit(0);
        }

        TBorrower borrower = new TBorrower(connFactory,topicName,rate);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("TBorrower application started");
        System.out.println("Press enter to quit");
        reader.readLine();
        borrower.exit();
    }
}
