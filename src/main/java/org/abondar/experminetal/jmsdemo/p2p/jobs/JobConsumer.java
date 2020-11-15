package org.abondar.experminetal.jmsdemo.p2p.jobs;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.ActiveMQConnectionFactory;


import javax.jms.*;


public class JobConsumer implements Command {

    private transient Connection connection;
    private transient Session session;
    private final String[] jobs = new String[]{"suspend","delete"};


    public void close() throws JMSException{
        if (connection !=null){
            connection.close();
        }
    }


    public Session getSession() {
        return session;
    }

    @Override
    public void execute() {
        try {
            initConnection();
            for (String job: jobs){
                Destination destination = getSession().createQueue("JOBS."+job);
                MessageConsumer messageConsumer = getSession().createConsumer(destination);
                messageConsumer.setMessageListener(new Listener(job));

            }
        } catch (JMSException ex){
            System.out.println("Failed to connect to broker");
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws JMSException {
        String brokerURL = "tcp://0.0.0.0:61616";
        ConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
        connection = factory.createConnection();
        connection.start();
        session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

    }


}
