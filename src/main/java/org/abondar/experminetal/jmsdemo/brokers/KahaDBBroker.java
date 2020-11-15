package org.abondar.experminetal.jmsdemo.brokers;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;

import java.io.File;


public class KahaDBBroker implements Command {
    private KahaDBStore kaha;
    @Override
    public void execute() {
        File fileDir = new File("/home/abondar/kahadb");

        kaha = new KahaDBStore();
        kaha.setDirectory(fileDir);

        kaha.setJournalMaxFileLength(1024*1204*100);
        kaha.setIndexWriteBatchSize(100);
        kaha.setEnableIndexWriteAsync(true);

        try {
            initConnection();
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void initConnection() throws Exception {
        BrokerService broker = new BrokerService();
        broker.setPersistenceAdapter(kaha);
        broker.addConnector("tcp://0.0.0.0:61616");
        broker.start();
    }
}
