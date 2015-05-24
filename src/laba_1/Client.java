package laba_1;

import laba_1.Impl.ClientImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by User on 01.04.2015.
 */
public class Client {

    public static void main(String[] args) throws Exception {
        //получения ссылки на удаленный объект - сервер

        //1 - порт для взаимодействия клиента с сервером
        Registry Servregistry= LocateRegistry.getRegistry(1);
        // создание строки, содержащей URL сервера в RMI
        String ServName = "rmi://localhost/doctor";
        medServer server = (medServer) Servregistry.lookup(ServName);
        //создание удаленного объекта - клиента
        ClientImpl client = new ClientImpl(server);

    }
}
