package org.abondar.experminetal.jmsdemo.p2p.jobs;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class JobProducer implements Command {

    private static String brokerURL = "tcp://0.0.0.0:61616";
    private static transient ConnectionFactory factory;
    private static final int count = 10;
    private static int total;
    private static int id = 10000000;
    private transient Connection connection;
    private transient Session session;
    private transient MessageProducer producer;
    private final String[] jobs = new String[]{"suspend", "delete"};


    public void execute() {

        try {
            initConnection();
            while (total < 1000) {
                for (int i = 0; i < count; i++) {
                   sendMessage();
                }
                total += count;
                System.out.println("Sent '" + count + "' of '" + total + "' job messages");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());
                }
            }
            close();
        } catch (JMSException ex){
            System.out.println("Failed to connect to broker");
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws JMSException {
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
        System.out.println("Sending: id: " + ((ObjectMessage) message).getObject() + " on queue: " + destination);
        producer.send(destination, message);
    }
}
