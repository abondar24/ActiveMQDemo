package org.abondar.experminetal.jmsdemo.brokers;


import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.security.AuthenticationUser;
import org.apache.activemq.security.SimpleAuthenticationPlugin;

import java.util.ArrayList;
import java.util.List;

public class BrokerWithSecurity {
    public static void main(String[] args) throws Exception {
        BrokerService brokerService = new BrokerService();
        brokerService.setBrokerName("ss-217");
        brokerService.setDataDirectory("data/");

        SimpleAuthenticationPlugin plugin = new SimpleAuthenticationPlugin();

        List<AuthenticationUser> users = new ArrayList<>();
        users.add(new AuthenticationUser("admin", "password", "admins,publishers,consumers"));
        users.add(new AuthenticationUser("publisher", "password", "publishers,consumers"));
        users.add(new AuthenticationUser("consumer", "password", "consumers"));
        users.add(new AuthenticationUser("guest", "password", "guests"));
        plugin.setUsers(users);

        brokerService.addConnector("tcp://0.0.0.0:61616");
        brokerService.start();
    }
}
