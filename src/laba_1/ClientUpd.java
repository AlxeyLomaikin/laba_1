package laba_1;

import laba_1.model.doctor;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by User on 13.05.2015.
 */
public interface ClientUpd extends Remote {
    public void Update () throws RemoteException;
}
