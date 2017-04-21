package org.abondar.experminetal.jmsdemo.p2p.jobs;


import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


public class Listener implements MessageListener {

    private String job;

    public Listener(String job){
        this.job = job;
    }


    @Override
    public void onMessage(Message message) {
        System.out.println(message.toString());
        try {
            System.out.println(job + " id:"+((ObjectMessage)message).getObject());
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
