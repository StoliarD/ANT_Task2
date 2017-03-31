package edu.ant_task2;

import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Dmitry on 29.03.2017.
 */
public interface ServerInterface extends Remote {
    boolean send(long date, String parameter, BigDecimal value) throws RemoteException;
}
