package laba_1;

import javafx.scene.control.TextField;
import laba_1.model.doctor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by User on 01.04.2015.
 */
public interface medServer extends Remote {
  public void addClient (String objectName) throws RemoteException, NotBoundException;
  public int getFreePort () throws RemoteException;
  public void add(doctor doctor) throws Exception;
  public boolean delAll() throws RemoteException;
  public boolean delElement(int ID) throws Exception;
  public boolean edit(int ID, doctor doctor) throws Exception;
  public ArrayList<doctor> getAll() throws RemoteException;
  public ArrayList<doctor> findByID(int ID) throws RemoteException;
  public ArrayList<doctor> findByName(String name) throws RemoteException;
  public ArrayList<doctor> findBySurname(String surname) throws RemoteException;
  public ArrayList<doctor> findByOccupation(String occupation) throws RemoteException;
  public ArrayList<doctor> findByAge(int age) throws RemoteException;
  public ArrayList<doctor> findByAll(String id, String name, String surname,
                                     String occ, String age) throws RemoteException, NumberFormatException;
}
