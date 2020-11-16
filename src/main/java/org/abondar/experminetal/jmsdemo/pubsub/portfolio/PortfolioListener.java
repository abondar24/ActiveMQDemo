package org.abondar.experminetal.jmsdemo.pubsub.portfolio;


import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.text.DecimalFormat;

public class PortfolioListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            MapMessage mapMessage = (MapMessage) message;
            String stock = mapMessage.getString("stock");
            double price = mapMessage.getDouble("price");
            double offer = mapMessage.getDouble("offer");
            boolean up = mapMessage.getBoolean("up");
            DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
            System.out.println(stock + "\t"+df.format(price)+"\t"+df.format(offer)+"\t"+(up?"up":"down"));
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }
}
