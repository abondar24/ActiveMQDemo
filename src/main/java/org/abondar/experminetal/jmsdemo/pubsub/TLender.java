package org.abondar.experminetal.jmsdemo.pubsub;


import org.abondar.experminetal.jmsdemo.command.Command;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


public class TLender implements Command {
    private TopicConnection connection = null;
    private TopicSession session = null;
    private Topic topic = null;


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



    @Override
    public void execute() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("TLender Application Started");
        System.out.println("Press enter to quit");
        System.out.println("Enter: New Rate");
        System.out.println("\ne.g 6.8");

        try {
            while (true) {
                System.out.print("> ");
                String rate = reader.readLine();
                if (rate == null || rate.trim().length() <= 0) {
                    exit();
                }

                double newRate = Double.parseDouble(rate);
                initConnection();
                publishRate(newRate);
            }
        } catch (Exception ex){
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
        session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        String topicName = env.getProperty("topic.rates");
        topic = (Topic) context.lookup(topicName);
        connection.start();
    }
}


