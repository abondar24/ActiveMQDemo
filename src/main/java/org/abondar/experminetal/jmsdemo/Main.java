package org.abondar.experminetal.jmsdemo;


import org.abondar.experminetal.jmsdemo.command.CommandSwitcher;

public class Main {

    public static void main(String[] args) {
        CommandSwitcher switcher = new CommandSwitcher();
        if (args.length==0){
            System.out.println("Missing argument. Please check documentation for available argynebts");
            System.exit(0);
        }
        String cmd = args[0].toUpperCase();
        switcher.executeCommand(cmd);
    }
}
