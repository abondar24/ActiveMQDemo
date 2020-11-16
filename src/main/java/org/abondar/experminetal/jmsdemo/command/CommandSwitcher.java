package org.abondar.experminetal.jmsdemo.command;

import org.abondar.experminetal.jmsdemo.brokers.BrokerWithSecurity;
import org.abondar.experminetal.jmsdemo.brokers.KahaDBBroker;
import org.abondar.experminetal.jmsdemo.p2p.QBorrower;
import org.abondar.experminetal.jmsdemo.p2p.QLender;
import org.abondar.experminetal.jmsdemo.p2p.jobs.JobConsumer;
import org.abondar.experminetal.jmsdemo.p2p.jobs.JobProducer;

public class CommandSwitcher {

    private final CommandExecutor executor;

    public CommandSwitcher() {
        this.executor = new CommandExecutor();
    }


    public void executeCommand(String cmd){
        try {
            switch (Commands.valueOf(cmd)){
                case JOB:
                    JobConsumer jobConsumer = new JobConsumer();
                    JobProducer jobProducer = new JobProducer();

                    executor.executeCommand(jobProducer);
                    executor.executeCommand(jobConsumer);
                    break;

                case SEC:
                    BrokerWithSecurity bSec = new BrokerWithSecurity();
                    executor.executeCommand(bSec);
                    break;

                case KAHA:
                    KahaDBBroker kaha = new KahaDBBroker();
                    executor.executeCommand(kaha);
                    break;

                case QL:
                    QLender lender = new QLender();
                    executor.executeCommand(lender);
                    break;

                case QBR:
                    QBorrower qBorrower = new QBorrower();
                    executor.executeCommand(qBorrower);
                    break;
            }
        } catch (IllegalArgumentException ex){
            System.out.println("Check documentation for command list");
            System.exit(1);
        }

    }
}
