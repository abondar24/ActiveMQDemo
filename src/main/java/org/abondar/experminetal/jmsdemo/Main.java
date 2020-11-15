package org.abondar.experminetal.jmsdemo;


import org.abondar.experminetal.jmsdemo.command.CommandSwitcher;

public class Main {

    public static void main(String[] args) {
        CommandSwitcher switcher = new CommandSwitcher();
        String cmd = args[0].toUpperCase();
        switcher.executeCommand(cmd);
    }
}
