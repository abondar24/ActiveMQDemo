package org.abondar.experminetal.jmsdemo.p2p;


import org.abondar.experminetal.jmsdemo.command.Command;

import javax.jms.ConnectionMetaData;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class Metadata implements Command {


    @Override
    public void execute() {
        try {
            Properties env = new Properties();
            InputStream is = Metadata.class.getClassLoader().getResourceAsStream("qbl.properties");
            env.load(is);

            Context context = new InitialContext(env);
            String queueFactory = env.getProperty("connectionFactoryNames");
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup(queueFactory);

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
            connection.close();
            System.exit(0);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void initConnection() throws Exception {

    }
}
