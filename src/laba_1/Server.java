package laba_1;

import laba_1.Impl.medServerImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by User on 01.04.2015.
 */
public class Server {
        public static void main(String[] args) throws Exception {
        // создание удаленного объекта
        medServerImpl server = new medServerImpl();
        // регистрация удаленного объекта в реестре rmiregistry
        Registry registry = LocateRegistry.createRegistry(1);
        // задание имени удаленного объекта
        String MyServer = "rmi://localhost/doctor";
        //связываем имя с нашим удаленным объектом
        registry.rebind(MyServer, server);
        System.out.println("Running...");
        }
}
