package org.abondar.experminetal.jmsdemo.sync;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;

public class SyncServer implements MessageListener, Command {

    private final String brokerUrl = "tcp://0.0.0.0:61616";

    private BrokerService broker;
    private Session session;
    private MessageProducer producer;
    private MessageConsumer consumer;


    private void createBroker() throws Exception {
        broker = new BrokerService();
        broker.setPersistent(false);
        broker.setUseJmx(false);
        broker.addConnector(brokerUrl);
        broker.start();
    }

    private void setupConsumer() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        Connection connection;
        connection = connectionFactory.createConnection();
        connection.start();

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        String requestQueue = "requests";
        Destination adminQueue = session.createQueue(requestQueue);

        producer = session.createProducer(null);

        consumer = session.createConsumer(adminQueue);
        consumer.setMessageListener(this);
    }

    public void disableConnection() throws Exception {
        producer.close();
        consumer.close();
        session.close();
        broker.stop();
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage resp = this.session.createTextMessage();
            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();
                resp.setText(handleRequest(messageText));
            }

            resp.setJMSCorrelationID(message.getJMSCorrelationID());
            producer.send(message.getJMSReplyTo(), resp);
        } catch (JMSException ex) {
            System.err.println(ex.getMessage());
        }
    }


    public String handleRequest(String messageText){
        return "Response to '" + messageText +"'";
    }


    @Override
    public void execute() {

        try {
           initConnection();
            System.out.println();
            System.out.println("Press any key to stop the server");
            System.out.println();

            System.in.read();
            disableConnection();
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws Exception {
        createBroker();
        setupConsumer();
    }
}
