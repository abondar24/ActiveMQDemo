package org.abondar.experminetal.jmsdemo.brokers;


import org.abondar.experminetal.jmsdemo.command.Command;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;

import java.util.ArrayList;
import java.util.List;

public class BrokerWithSecurity implements Command {


    @Override
    public void execute() {

        SimpleAuthenticationPlugin plugin = new SimpleAuthenticationPlugin();

        List<AuthenticationUser> users = new ArrayList<>();
        users.add(new AuthenticationUser("admin", "password", "admins,publishers,consumers"));
        users.add(new AuthenticationUser("publisher", "password", "publishers,consumers"));
        users.add(new AuthenticationUser("consumer", "password", "consumers"));
        users.add(new AuthenticationUser("guest", "password", "guests"));
        plugin.setUsers(users);
      try {
          initConnection();
      } catch (Exception ex){
          System.out.println(ex.getMessage());
          System.exit(1);
      }

    }

    @Override
    public void initConnection() throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("ss-217");
        brokerService.setDataDirectory("data/");

        brokerService.addConnector("tcp://0.0.0.0:61616");
        brokerService.start();
    }
}
