package org.abondar.experminetal.jmsdemo.pubsub;


import org.abondar.experminetal.jmsdemo.command.Command;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class TBorrower implements MessageListener, Command {

    private TopicConnection connection = null;
    private double currentRate;


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


    @Override
    public void execute() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("TBorrower application started");
            System.out.println("Press enter to quit");
            System.out.println("Enter: Rate");
            System.out.println("e.g 6.8");
            String rate = reader.readLine();
            currentRate = Double.parseDouble(rate);

            initConnection();
            Thread.sleep(100000);
            exit();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }


    }


    @Override
    public void initConnection() throws Exception {
        Properties env = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("tbl.properties");
        env.load(is);


        Context context = new InitialContext(env);
        String connFactory = env.getProperty("connectionFactoryNames");
        TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup(connFactory);

        connection = factory.createTopicConnection();
        TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        String topicName = env.getProperty("topic.rates");
        Topic topic = (Topic) context.lookup(topicName);

        TopicSubscriber subscriber = session.createSubscriber(topic);
        subscriber.setMessageListener(this);
        connection.start();

        System.out.println("Waiting for loan requests");
    }
}
