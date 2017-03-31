package edu.ant_task2.endpt;

import edu.ant_task2.ServerInterface;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by Dmitry on 29.03.2017.
 */
public class Server implements ServerInterface {
    static Params params;
    private static int count;

    public Server() {
        String dir = new File(".").getAbsolutePath();
        dir = dir.substring(0,dir.length()-1);
        params = new Params(dir + "\\params");
    }

    @Override
    public boolean send(long date, String parameter, BigDecimal value) {
        count++;
        System.out.println(new Date(date) + ", " + parameter + ", " + value);
        return params.add(parameter, value);
    }


    private static void console() {
        String message = "choose parameter or write 'stat'";
        Scanner in = new Scanner(System.in);
        System.out.println(message);
        while (true) {
            if (in.hasNext()) {
                String s = in.next();
                if (s.equalsIgnoreCase("stat")) {
                    System.out.println("stat - messages:" + count + " ; unique params: " + params.getParamCount());
                } else {
                    System.out.println(params.getParamStat(s.trim()));

                }
            }
        }
    }

    public static void main(String args[]) throws IOException {
        try {
            Server server = new Server();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);

            // Bind the remote object's stub in the registry
            final Registry registry = LocateRegistry.createRegistry(9001);
            registry.bind("MyServerName", stub);

            System.out.println("Server ready");
        } catch (AlreadyBoundException e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
        console();
    }
}
