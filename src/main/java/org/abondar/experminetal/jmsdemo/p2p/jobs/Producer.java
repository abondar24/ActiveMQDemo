package org.abondar.experminetal.jmsdemo.p2p.jobs;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Hashtable;
import java.util.Map;

public class Producer {

    protected static String brokerURL = "tcp://172.17.0.3:61616";
    protected static transient ConnectionFactory factory;
    protected transient Connection connection;
    protected transient Session session;
    protected transient MessageProducer producer;


    private String jobs[] = new String[]{"suspend", "delete"};
    private static int count = 10;
    private static int total;
    private static int id = 10000000;


    public Producer() throws JMSException {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(null);
    }

    public void close() throws JMSException {
        if (connection != null) {
            connection.close();
        }
    }

    protected void sendMessage() throws JMSException {
        int idx;
        while (true) {
            idx = (int) Math.round(jobs.length * Math.random());
            if (idx < jobs.length) {
                break;
            }
        }

        String job = jobs[idx];
        Destination destination = session.createQueue("JOBS." + job);
        Message message = session.createObjectMessage(id++);
        System.out.println("Sending: id: "+ ((ObjectMessage)message).getObject() + " on queue: " + destination);
        producer.send(destination, message);
    }


    public static void main(String[] args) throws JMSException {
        Producer publisher = new Producer();
        while (total < 1000) {
            for (int i = 0; i < count; i++) {
                publisher.sendMessage();
            }
            total += count;
            System.out.println("Sent '" + count + "' of '" + total + "' job messages");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }
        }
        publisher.close();
    }
}
