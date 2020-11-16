package org.abondar.experminetal.jmsdemo.pubsub.portfolio;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


public class PortfolioConsumer implements Command {

    private static transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Session session;



    public void close() throws JMSException{
        if (connection !=null){
            connection.close();
        }
    }

    @Override
    public void execute() {
        String[] stocks = new String[]{"CSCO", "GOOGL", "MS", "APPL"};

        try {
            initConnection();
            for (String stock: stocks){
                Destination destination = session.createTopic("STOCKS."+stock);
                MessageConsumer messageConsumer = session.createConsumer(destination);
                messageConsumer.setMessageListener(new PortfolioListener());
            }
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws Exception {
        String brokerURL = "tcp://0.0.0.0:61616";
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
    }
}
