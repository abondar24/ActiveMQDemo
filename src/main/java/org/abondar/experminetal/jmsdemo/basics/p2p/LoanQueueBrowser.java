package org.abondar.experminetal.jmsdemo.basics.p2p;


import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;


public class LoanQueueBrowser {

    public static void main(String[] args) throws Exception {
        Properties env = new Properties();
        InputStream is = LoanQueueBrowser.class.getClassLoader().getResourceAsStream("qbl.properties");
        env.load(is);

        Context context = new InitialContext(env);
        QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("QueueFactory");
        QueueConnection connection = factory.createQueueConnection();
        connection.start();

        Queue queue = (Queue) context.lookup("queueResp");
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
    }
}
