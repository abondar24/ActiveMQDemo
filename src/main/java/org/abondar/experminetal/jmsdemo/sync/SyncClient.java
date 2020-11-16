package org.abondar.experminetal.jmsdemo.sync;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.UUID;

public class SyncClient implements MessageListener, Command {

    private Connection connection;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;

    private Destination tempDest;



    public void disableConnection() throws JMSException {
        producer.close();
        consumer.close();;
        session.close();
        connection.close();
    }

    public void request(String req) throws JMSException {
        System.out.println("Requesting: "+req);
        TextMessage txtMessage = session.createTextMessage();
        txtMessage.setText(req);
        txtMessage.setJMSReplyTo(tempDest);

        String correlationID = UUID.randomUUID().toString();
        txtMessage.setJMSCorrelationID(correlationID);
        this.producer.send(txtMessage);
    }

    @Override
    public void onMessage(Message message) {
        try {
           System.out.println("Received response for: "+((TextMessage)message).getText());
        } catch (JMSException ex) {
            System.err.println(ex.getMessage());
        }
    }


    @Override
    public void execute() {

        try {
            initConnection();
            int i = 0;
            while (i++ < 10){
                request("REQUEST-"+i);
            }
            Thread.sleep(3000);
            disableConnection();
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws Exception {
        String brokerUrl = "tcp://0.0.0.0:61616";
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        String requestQueue = "requests";
        Destination adminQueue = session.createQueue(requestQueue);

        producer = session.createProducer(adminQueue);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        tempDest = session.createTemporaryQueue();
        consumer = session.createConsumer(tempDest);
        consumer.setMessageListener(this);
    }
}
