package org.abondar.experminetal.jmsdemo.pubsub.portfolio;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.Hashtable;
import java.util.Map;

public class PortfolioPublisher implements Command {

    protected static int count = 10;
    protected static int total;
    protected static String brokerURL = "tcp://localhost:61616";
    protected static transient ConnectionFactory factory;
    protected int MAX_DELTA_PERCENT = 1;
    protected Map<String, Double> LAST_PRICES = new Hashtable<>();
    protected transient Connection connection;
    protected transient Session session;
    protected transient MessageProducer producer;



    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }

    protected void sendMessage(String[] stocks) throws JMSException {
        int idx;
        while (true) {
            idx = (int) Math.round(stocks.length * Math.random());
            if (idx < stocks.length) {
                break;
            }
        }

        String stock = stocks[idx];
        Destination destination = session.createTopic("STOCKS." + stock);
        Message message = createStockMessage(stock, session);
        System.out.println("Sending on destination: " + destination);
        producer.send(destination, message);
    }

    protected Message createStockMessage(String stock, Session session) throws JMSException {
        Double value = LAST_PRICES.get(stock);
        if (value == null) {
            value = Math.random() * 100;
        }

        double oldPrice = value;
        value = mutatePrice(oldPrice);
        LAST_PRICES.put(stock, value);
        double price = value;
        double offer = price * 1.001;

        boolean up = (price > oldPrice);
        MapMessage message = session.createMapMessage();
        message.setString("stock", stock);
        message.setDouble("price", price);
        message.setDouble("offer", offer);
        message.setBoolean("up", up);
        return message;
    }

    protected double mutatePrice(double price) {
        double percentChange = (2 * Math.random() * MAX_DELTA_PERCENT) - MAX_DELTA_PERCENT;

        return price * (100 + percentChange) / 100;
    }

    @Override
    public void execute() {
        String[] stocks = new String[]{"CSCO", "GOOGL", "MS", "APPL"};

        try {
            initConnection();
            while (total < 1000) {
                for (int i = 0; i < count; i++) {
                    sendMessage(stocks);
                }
                total += count;
                System.out.println("Published '" + count + "' of '" + total + "' price messages");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            close();
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws Exception {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
    }
}
