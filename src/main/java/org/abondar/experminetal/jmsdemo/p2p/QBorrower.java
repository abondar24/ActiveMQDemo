package org.abondar.experminetal.jmsdemo.p2p;


import org.abondar.experminetal.jmsdemo.command.Command;

import javax.jms.JMSException;
import javax.jms.MapMessage;
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
import java.util.StringTokenizer;

public class QBorrower implements Command {
    private QueueConnection connection = null;
    private QueueSession session = null;
    private Queue respQueue = null;
    private Queue reqQueue = null;


    private void sendLoanRequest(double salary, double loanAmt) {
        try {
            MapMessage msg = session.createMapMessage();
            msg.setDouble("Salary", salary);
            msg.setDouble("LoanAmount", loanAmt);
            msg.setJMSReplyTo(respQueue);

            QueueSender sender = session.createSender(reqQueue);
            sender.send(msg);

            String filter = "JMSCorrelationID= '" + msg.getJMSMessageID() + "'";
            QueueReceiver receiver = session.createReceiver(respQueue, filter);
            TextMessage tmsg = (TextMessage) receiver.receive(3000);
            if (tmsg == null) {
                System.out.println("Qlender not responding");
            } else {
                System.out.println("Loan request was " + tmsg.getText());
            }
        } catch (JMSException ex) {
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

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("QBorrower Application Started");
        System.out.println("Press enter to quit");
        System.out.println("Enter: Salary,Loan Amount");
        System.out.println("\ne.g 50000, 120000");

        try {

            initConnection();
            while (true) {
                System.out.print("> ");
                String loanReq = reader.readLine();
                if (loanReq == null || loanReq.trim().length() <= 0) {
                    exit();
                }

                StringTokenizer st = new StringTokenizer(loanReq, ",");
                double salary = Double.parseDouble(st.nextToken().trim());
                double loanAmt = Double.parseDouble(st.nextToken().trim());

                sendLoanRequest(salary, loanAmt);
            }
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

        String reqQueue = env.getProperty("queue.queueReq");
        String respQueue = env.getProperty("queue.queueResp");
        String connFactory = env.getProperty("connectionFactoryNames");

        Context ctx = new InitialContext(env);
        QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(connFactory);
        connection = factory.createQueueConnection();

        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        this.reqQueue = (Queue) ctx.lookup(reqQueue);
        this.respQueue = (Queue) ctx.lookup(respQueue);

        connection.start();

    }
}
