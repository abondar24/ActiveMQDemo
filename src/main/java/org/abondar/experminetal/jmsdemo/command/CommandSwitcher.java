package org.abondar.experminetal.jmsdemo.command;

import org.abondar.experminetal.jmsdemo.brokers.BrokerWithSecurity;
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

                case SEC:
                    BrokerWithSecurity bSec = new BrokerWithSecurity();
                    executor.executeCommand(bSec);
            }
        } catch (IllegalArgumentException ex){
            System.out.println("Check documentation for command list");
            System.exit(1);
        }

    }
}
