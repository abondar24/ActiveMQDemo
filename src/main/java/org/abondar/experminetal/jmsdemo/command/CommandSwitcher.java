package org.abondar.experminetal.jmsdemo.command;

import org.abondar.experminetal.jmsdemo.p2p.jobs.JobConsumer;
import org.abondar.experminetal.jmsdemo.p2p.jobs.JobProducer;

public class CommandSwitcher {

    private CommandExecutor executor;

    public CommandSwitcher() {
        this.executor = new CommandExecutor();
    }


    public void executeCommand(String cmd){
        try {
            switch (Commands.valueOf(cmd)){
                case JOB:
                    JobConsumer jobConsumer = new JobConsumer();
                    JobProducer jobProducer = new JobProducer();

                    jobProducer.execute();
                    jobConsumer.execute();
            }
        } catch (IllegalArgumentException ex){
            System.out.println("Check documentation for command list");
            System.exit(1);
        }

    }
}
