package org.abondar.experminetal.jmsdemo.brokers;


import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;

import java.io.File;


public class KahaDBBroker {
    public static void main(String[] args) throws Exception {
        BrokerService broker = new BrokerService();
        File fileDir = new File("/home/abondar/kahadb");

        KahaDBStore kaha = new KahaDBStore();
        kaha.setDirectory(fileDir);

        kaha.setJournalMaxFileLength(1024*1204*100);
        kaha.setIndexWriteBatchSize(100);
        kaha.setEnableIndexWriteAsync(true);

        broker.setPersistenceAdapter(kaha);
        broker.addConnector("tcp://0.0.0.0:61616");
        broker.start();
    }
}
