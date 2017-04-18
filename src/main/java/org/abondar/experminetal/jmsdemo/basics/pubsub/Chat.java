package org.abondar.experminetal.jmsdemo.basics.pubsub;


import javax.jms.*;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Chat implements MessageListener {

    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private String username;


    public Chat(String topicFactory, String topicName, String username) throws Exception {

        Properties env = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("chat.properties");
        env.load(is);

        InitialContext context = new InitialContext(env);

        TopicConnectionFactory connectionFactory = (TopicConnectionFactory) context.lookup(topicFactory);
        TopicConnection connection = connectionFactory.createTopicConnection();

        TopicSession pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        TopicSession subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic chatTopic = (Topic) context.lookup(topicName);

        TopicPublisher publisher = pubSession.createPublisher(chatTopic);
        TopicSubscriber subscriber = subSession.createSubscriber(chatTopic, null, true);

        subscriber.setMessageListener(this);

        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        this.username = username;

        connection.start();
    }


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

    public static void main(String[] args)throws Exception {

        if (args.length != 3) {
            System.out.println("Factory, Topic, or username missing");
        }

        Chat chat = new Chat(args[0], args[1], args[2]);
        BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in));

        while (true){
            String s = commandLine.readLine();
            if (s.equalsIgnoreCase("exit")){
                chat.close();
                System.exit(0);
            } else {
                chat.writeMessage(s);
            }
        }

    }

}
