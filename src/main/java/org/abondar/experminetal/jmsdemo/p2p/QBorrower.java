package org.abondar.experminetal.jmsdemo.p2p;


import javax.jms.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.StringTokenizer;

public class QBorrower {
    private QueueConnection connection = null;
    private QueueSession session = null;
    private Queue respQueue = null;
    private Queue reqQueue = null;

    public QBorrower(String connFactory, String reqQueue,String respQueue) {

        try {
            Properties env = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("qbl.properties");
            env.load(is);

            Context ctx = new InitialContext(env);
            QueueConnectionFactory factory = (QueueConnectionFactory) ctx.lookup(connFactory);
            connection = factory.createQueueConnection();

            session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

            this.reqQueue = (Queue) ctx.lookup(reqQueue);
            this.respQueue = (Queue) ctx.lookup(respQueue);

            connection.start();
        } catch (IOException | JMSException | NamingException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private void sendLoanRequest(double salary, double loanAmt){
        try {
            MapMessage msg = session.createMapMessage();
            msg.setDouble("Salary",salary);
            msg.setDouble("LoanAmount",loanAmt);
            msg.setJMSReplyTo(respQueue);

            QueueSender sender = session.createSender(reqQueue);
            sender.send(msg);

            String filter = "JMSCorrelationID= '" + msg.getJMSMessageID()+"'";
            QueueReceiver receiver = session.createReceiver(respQueue,filter);
            TextMessage tmsg = (TextMessage)receiver.receive(3000);
            if (tmsg == null){
                System.out.println("Qlender not responding");
            } else {
                System.out.println("Loan request was "+ tmsg.getText());
            }
        } catch (JMSException ex){
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private void exit() throws JMSException{
        connection.close();
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        String queueFactory = null;
        String reqQueue = null;
        String respQueue = null;

        if (args.length == 3) {
           queueFactory = args[0];
           reqQueue = args[1];
           respQueue = args[2];
        } else {
            System.out.println("Invalid Arguments. Usage: ");
            System.out.println("java QBorrower factory requestQueue responseQueue");
            System.exit(0);
        }

        QBorrower borrower = new QBorrower(queueFactory,reqQueue,respQueue);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("QBorrower Application Started");
        System.out.println("Press enter to quit");
        System.out.println("Enter: Salary,Loan Amount");
        System.out.println("\ne.g 50000, 120000");

        while (true){
            System.out.print("> ");
            String loanReq = reader.readLine();
            if (loanReq == null || loanReq.trim().length() <=0){
                borrower.exit();
            }

            StringTokenizer st  = new StringTokenizer(loanReq,",");
            double salary = Double.valueOf(st.nextToken().trim());
            double loanAmt = Double.valueOf(st.nextToken().trim());

            borrower.sendLoanRequest(salary,loanAmt);
        }
    }
}
