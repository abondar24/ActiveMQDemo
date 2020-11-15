package org.abondar.experminetal.jmsdemo.command;

import javax.jms.JMSException;

public interface Command {

    void execute();

     void initConnection() throws JMSException;
}
