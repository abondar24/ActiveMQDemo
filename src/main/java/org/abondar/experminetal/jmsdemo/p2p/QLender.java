package org.abondar.experminetal.jmsdemo.p2p;


import org.abondar.experminetal.jmsdemo.command.Command;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class QLender implements MessageListener, Command {

    private QueueConnection connection = null;
    private QueueSession session = null;


    @Override
    public void onMessage(Message message) {
        try {
            boolean accepted;

            MapMessage msg = (MapMessage) message;
            double salary = msg.getDouble("Salary");
            double loanAmt = msg.getDouble("LoanAmount");

            if (loanAmt < 200000) {
                accepted = (salary / loanAmt) > .25;
            } else {
                accepted = (salary / loanAmt) > .33;
            }
            System.out.println("" + "Percent = " +
                    (salary / loanAmt) + ", loan is " + (accepted ? "Accepted!" : "Declined"));

            TextMessage tmsg = session.createTextMessage();
            tmsg.setText(accepted ? "Accepted!" : "Declined");
            tmsg.setJMSCorrelationID(message.getJMSMessageID());

            QueueSender sender = session.createSender((Queue) message.getJMSReplyTo());
            sender.send(tmsg);

            System.out.println("\nWaiting for loan requests...");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(2);
        }
    }

    private void exit() throws JMSException {
        connection.close();
        System.exit(0);
    }


    @Override
    public void execute() {

        try {
            initConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("QLender application started");
            System.out.println("Press enter to quit");
            reader.readLine();
            exit();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }


    }

    @Override
    public void initConnection() throws Exception {


        Properties env = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream("qbl.properties");
        env.load(is);
        String rq = env.getProperty("queue.queueReq");
        String connFactory = env.getProperty("connectionFactoryNames");

        Context ctx = new InitialContext(env);
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(connFactory);
        connection = factory.createQueueConnection();
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue reqQueue = (Queue) ctx.lookup(rq);
        connection.start();

        QueueReceiver receiver = session.createReceiver(reqQueue);
        receiver.setMessageListener(this);

        System.out.println("Waiting for loan requests...");


    }
}
