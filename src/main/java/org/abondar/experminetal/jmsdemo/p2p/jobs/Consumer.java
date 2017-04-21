package org.abondar.experminetal.jmsdemo.p2p.jobs;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


public class Consumer {

    private static String brokerURL = "tcp://172.17.0.3:61616";
    private static transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Session session;
    private String jobs[] = new String[]{"suspend","delete"};

    public Consumer() throws JMSException {
        factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

    }

    public void close() throws JMSException{
        if (connection !=null){
            connection.close();
        }
    }



    public Session getSession() {
        return session;
    }

    public static void main(String[] args) throws JMSException {
        Consumer consumer = new Consumer();
        for (String job: consumer.jobs){
            Destination destination = consumer.getSession().createQueue("JOBS."+job);
            MessageConsumer messageConsumer = consumer.getSession().createConsumer(destination);
            messageConsumer.setMessageListener(new Listener(job));

        }
    }




}
