package org.abondar.experminetal.jmsdemo.pubsub;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Chat implements MessageListener, Command {

    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;


    @Override
    public void onMessage(Message message) {
        try {
            TextMessage msg = (TextMessage) message;
            System.out.println(msg.getText());
        } catch (JMSException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected void writeMessage(String text) throws JMSException {
        TextMessage message = pubSession.createTextMessage();
        message.setText(username + ": " + text);
        publisher.publish(message);
    }

    public void close() throws JMSException {
        connection.close();
    }

    @Override
    public void execute() {


        BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Enter username:");
            String username = commandLine.readLine();
            System.out.println("Now you can start chatting");
            initConnection(username);
            while (true){
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")){
                    close();
                    System.exit(0);
                } else {
                    writeMessage(s);
                }
            }
        } catch (Exception ex){
            System.out.println("Chat failure");
            System.exit(1);
        }

    }

    private void initConnection(String username) throws Exception{
        this.username = username;
        initConnection();
        connection.start();
    }

    @Override
    public void initConnection() throws Exception {
        Properties env = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("chat.properties");
        env.load(is);
        InitialContext context = new InitialContext(env);


        String topicFactoryName = env.getProperty("connectionFactoryNames");
        TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup(topicFactoryName);
        TopicConnection connection = connectionFactory.createTopicConnection();

        TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        String topicName = env.getProperty("topic.chatTopic");
        Topic chatTopic = (Topic) context.lookup(topicName);

        TopicPublisher publisher = pubSession.createPublisher(chatTopic);
        TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);

        subscriber.setMessageListener(this);

        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        this.connection.start();
    }
}
