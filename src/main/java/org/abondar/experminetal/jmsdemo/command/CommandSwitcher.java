package org.abondar.experminetal.jmsdemo.command;

import org.abondar.experminetal.jmsdemo.brokers.BrokerWithSecurity;
import org.abondar.experminetal.jmsdemo.brokers.KahaDBBroker;
import org.abondar.experminetal.jmsdemo.p2p.LoanQueueBrowser;
import org.abondar.experminetal.jmsdemo.p2p.Metadata;
import org.abondar.experminetal.jmsdemo.p2p.QBorrower;
import org.abondar.experminetal.jmsdemo.p2p.QLender;
import org.abondar.experminetal.jmsdemo.p2p.jobs.JobConsumer;
import org.abondar.experminetal.jmsdemo.p2p.jobs.JobProducer;
import org.abondar.experminetal.jmsdemo.pubsub.Chat;
import org.abondar.experminetal.jmsdemo.pubsub.TBorrower;
import org.abondar.experminetal.jmsdemo.pubsub.TLender;
import org.abondar.experminetal.jmsdemo.pubsub.portfolio.PortfolioConsumer;
import org.abondar.experminetal.jmsdemo.pubsub.portfolio.PortfolioPublisher;
import org.abondar.experminetal.jmsdemo.sync.SyncClient;
import org.abondar.experminetal.jmsdemo.sync.SyncServer;

public class CommandSwitcher {

    private final CommandExecutor executor;

    public CommandSwitcher() {
        this.executor = new CommandExecutor();
    }


    public void executeCommand(String cmd){
        try {
            switch (Commands.valueOf(cmd)){
                case CHAT:
                    Chat chat = new Chat();
                    executor.executeCommand(chat);
                    break;

                case JC:
                    JobConsumer jobConsumer = new JobConsumer();
                    executor.executeCommand(jobConsumer);
                    break;

                case JP:
                    JobProducer jobProducer = new JobProducer();
                    executor.executeCommand(jobProducer);
                    break;

                case KAHA:
                    KahaDBBroker kaha = new KahaDBBroker();
                    executor.executeCommand(kaha);
                    break;

                case MD:
                    Metadata metadata = new Metadata();
                    executor.executeCommand(metadata);
                    break;

                case QB:
                    LoanQueueBrowser lqb = new LoanQueueBrowser();
                    executor.executeCommand(lqb);
                    break;

                case QBR:
                    QBorrower qBorrower = new QBorrower();
                    executor.executeCommand(qBorrower);
                    break;

                case QL:
                    QLender lender = new QLender();
                    executor.executeCommand(lender);
                    break;

                case PFC:
                    PortfolioConsumer consumer = new PortfolioConsumer();
                    executor.executeCommand(consumer);
                    break;

                case PFP:
                    PortfolioPublisher publisher = new PortfolioPublisher();
                    executor.executeCommand(publisher);
                    break;

                case SC:
                    SyncClient syncClient = new SyncClient();
                    executor.executeCommand(syncClient);
                    break;

                case SEC:
                    BrokerWithSecurity bSec = new BrokerWithSecurity();
                    executor.executeCommand(bSec);
                    break;

                case SS:
                    SyncServer syncServer = new SyncServer();
                    executor.executeCommand(syncServer);
                    break;

                case TBR:
                    TBorrower tBorrower = new TBorrower();
                    executor.executeCommand(tBorrower);
                    break;

                case TL:
                    TLender tLender = new TLender();
                    executor.executeCommand(tLender);
                    break;
            }
        } catch (IllegalArgumentException ex){
            System.out.println("Check documentation for command list");
            System.exit(1);
        }

    }
}
