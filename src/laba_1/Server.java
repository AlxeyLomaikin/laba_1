package laba_1;

import laba_1.Impl.medServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by User on 01.04.2015.
 */
public class Server {
        public static void main(String[] args) throws Exception {
        // �������� ���������� �������
        medServerImpl server = new medServerImpl();
        // ����������� ���������� ������� � ������� rmiregistry
        Registry registry = LocateRegistry.createRegistry(1);
        // ������� ����� ���������� �������
        String MyServer = "rmi://localhost/doctor";
        //��������� ��� � ����� ��������� ��������
        registry.rebind(MyServer, server);
        System.out.println("Running...");
        }
}
