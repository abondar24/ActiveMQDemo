package org.abondar.experminetal.jmsdemo.p2p;


import org.abondar.experminetal.jmsdemo.command.Command;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


public class LoanQueueBrowser implements Command {


    @Override
    public void execute() {

        try {
            Properties env = new Properties();
            InputStream is = LoanQueueBrowser.class.getClassLoader().getResourceAsStream("qbl.properties");
            env.load(is);

            Context context = new InitialContext(env);
            String queueFactory = env.getProperty("connectionFactoryNames");
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(queueFactory);
            QueueConnection connection = factory.createQueueConnection();
            connection.start();

            String qr = env.getProperty("queue.queueResp");
            Queue queue = (Queue) context.lookup(qr);
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            QueueBrowser browser = session.createBrowser(queue);

            Enumeration e = browser.getEnumeration();
            while (e.hasMoreElements()) {
                TextMessage msg = (TextMessage) e.nextElement();
                System.out.println("Browsing: " + msg.getText());
            }

            browser.close();
            connection.close();
            System.exit(0);
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }


    }

    @Override
    public void initConnection() throws Exception {

    }
}
