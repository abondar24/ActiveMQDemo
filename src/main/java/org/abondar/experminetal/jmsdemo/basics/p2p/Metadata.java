package org.abondar.experminetal.jmsdemo.basics.p2p;


import javax.jms.ConnectionMetaData;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class Metadata {
    public static void main(String[] args) {
        try {
            Properties env = new Properties();
            InputStream is = Metadata.class.getClassLoader().getResourceAsStream("qbl.properties");
            env.load(is);

            Context context = new InitialContext(env);
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("QueueFactory");

            QueueConnection connection = factory.createQueueConnection();
            ConnectionMetaData metaData = connection.getMetaData();

            System.out.println("JMS Version: " +
                    metaData.getJMSMajorVersion() + "." +
                    metaData.getJMSMinorVersion());

            System.out.println("JMS Provider: " + metaData.getJMSProviderName());
            System.out.println("JMSX Properties Supported: ");
            Enumeration e = metaData.getJMSXPropertyNames();
            while (e.hasMoreElements()) {
                System.out.println("  " + e.nextElement());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }
}
