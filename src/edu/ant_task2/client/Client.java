package edu.ant_task2.client;

/**
 * Created by Dmitry on 29.03.2017.
 */
import edu.ant_task2.ServerInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class Client {
    private ServerInterface stub;
    private String dateFormat;
    private SimpleDateFormat simpleDateFormat;
    private Messages messages;


    private Client(ServerInterface stub) {
        this.stub = stub;
        dateFormat = "dd.MM.yy HH:mm";
        simpleDateFormat = new SimpleDateFormat(dateFormat);

        String dir = new File(".").getAbsolutePath();
        dir = dir.substring(0,dir.length()-1);
        String iniFileName = dir + "/settings.ini";
        messages = new Messages(dir + "/messages");
        try (BufferedReader br = new BufferedReader(new FileReader(new File(iniFileName)))) {
            String[] ss = br.readLine().split(" ");
            if (ss[1].equalsIgnoreCase("cmd"))
                cmdInput();
            else if (ss[1].equalsIgnoreCase("csv")) {
                long delay = Long.parseLong(br.readLine().split(" ")[1]);
                ss = br.readLine().split("\\(")[0].split(",");
                HashMap<String,Integer> scvFormat = new HashMap<>();
                scvFormat.put(ss[0].trim(),0);
                scvFormat.put(ss[1].trim(),1);
                scvFormat.put(ss[2].trim(),2);
                csvMonitor(delay, scvFormat, dir+"/CSV/");
            } else
                System.out.println("wrong ini file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseInput(String inputString, HashMap<String,Integer> csvFormat) {
        String[] input = inputString.split(",");
        try {
            Date date = simpleDateFormat.parse(input[csvFormat.get("date")].trim());
            String parameter = input[csvFormat.get("param")].trim();
            BigDecimal value = new BigDecimal(input[csvFormat.get("value")].trim());
            System.out.println("param: " + parameter);
            if (messages.checkMessage(date.getTime() + "," + parameter + "," + value)) {
                if (stub.send(date.getTime(), parameter, value))
                    System.out.println("sent to server");
                else System.out.println("error on server");
            } else
                System.out.println("not sent : duplicate");
        } catch (NumberFormatException e) {
            System.out.println("value incorrect input, try again");
        } catch (ParseException e1) {
            System.out.println("date incorrect input, try again");
        } catch (RemoteException e2) {
            System.out.println("no connection, restart pls");
//            e2.printStackTrace();
        }
    }

    private void cmdInput() {
        System.out.println("'CMD Input' mode:");
        String message = "input Date(" + dateFormat + "), String, BigDecimal; 'q' - quit";
        Scanner in = new Scanner(System.in);
        System.out.println(message);
        HashMap<String,Integer> scvFormat = new HashMap<>();
        scvFormat.put("date",0);
        scvFormat.put("param",1);
        scvFormat.put("value",2);
        while (true) {
            if (in.hasNextLine()) {
                String s = in.nextLine();
                if (s.equalsIgnoreCase("q"))
                    break;
                parseInput(s,scvFormat);
            }
        }
    }

    private void csvMonitor(long delay, HashMap<String,Integer> csvFormat, String dir) {
        System.out.println("'CSV Monitor' mode: ");
        System.out.println("close this window to quit");
        System.out.println();
        File folder = new File(dir);
        while (true) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println(line);
                            parseInput(line,csvFormat);
                            System.out.println();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    file.delete();
                }
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 9001;
        try {
            Registry registry = LocateRegistry.getRegistry(host,port);
            ServerInterface stub = (ServerInterface) registry.lookup("MyServerName");
            System.out.println("connected to server");
            new Client(stub);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("no connection");
        }
//              26.12.90 12:30, param, 1.0
    }
}
