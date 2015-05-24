package laba_1.Impl;

import laba_1.ClientUpd;
import laba_1.model.doctor;
import laba_1.medServer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by User on 02.04.2015.
 */
public class ClientImpl extends UnicastRemoteObject implements ClientUpd {
    //удаленный интерфейс
    private medServer server;
    private ArrayList<doctor> doctors;

    //обновляет данные на клиенте при любом изменении данных на сервере
    public void Update() throws RemoteException {
        this.doctors = server.getAll();
    }
    public ClientImpl(medServer server) throws Exception {
        this.server = server;
        this.doctors = server.getAll();

        //клиент становится сервером

        //получаем свободный порт от сервера
        int freePort = server.getFreePort();
        //порт, используемый для взаимодействия сервера с клиентом
        Registry ClientRegistry = LocateRegistry.createRegistry(freePort);
        //имя клиента в RMI
        String ClientName = "rmi://localhost/doc";
        //связываем имя с объектом-клиентом
        ClientRegistry.rebind(ClientName, this);
        //добавляем клиент в список клиентов на сервер
        server.addClient(ClientName);
        this.Menu();
    }

    private void Menu() throws Exception {
        int key = 0;
        boolean end = false;
        do {
            System.out.println("Main Menu:");
            System.out.println("0 - Print List on server");
            System.out.println("1 - Print List on client");
            System.out.println("2 - Add doctor");
            System.out.println("3 - Delete doctor");
            System.out.println("4 - Clear List");
            System.out.println("5 - Edit info about doctor");
            System.out.println("6 - Search in List");
            System.out.println("any key - Exit");
            System.out.print("\nEnter number:");
            key = EnterAndCheck();
            switch (key) {
                case 0: {
                    printList(server.getAll());
                    break;
                }
                case 1: {
                    printList(this.doctors);
                    break;
                }
                case 2: {
                    AddDoc();
                    break;
                }
                case 3: {
                    DelOne();
                    break;
                }
                case 4: {
                    this.doctors.clear();
                    ClearAll();
                    break;
                }
                case 5: {
                    EdInfo();
                    break;
                }
                case 6: {
                    SearchDoc();
                    break;
                }
                default: {
                    end = true;
                    break;
                }
            }
        } while (!end);
    }

    public void ErrorEmptyList() throws IOException, NotBoundException {
        System.out.println("List is Empty, press Enter");
        System.in.read();
    }

    //Просматриваем весь наш список
    public void printList(ArrayList<doctor> doctors) throws IOException, NotBoundException {
        if (doctors.isEmpty())
            ErrorEmptyList();
        else {
            for (doctor doctor : doctors) {
                System.out.println("\nID:" + doctor.getID());
                System.out.println("Name:" + doctor.getName());
                System.out.println("Surname:" + doctor.getSurname());
                System.out.println("Occupation:" + doctor.getOccupation());
                System.out.println("Age:" + doctor.getAge());
            }
            System.in.read();
        }
    }

    //Ввод целого положительного числа
    public int EnterAndCheck() throws IOException, NotBoundException {
        Scanner sc = new Scanner(System.in);
        int i = 0;
        while (true) {
            String inputText = sc.nextLine();
            try {
                i = Integer.parseInt(inputText);
                if (i < 0) {
                    System.out.println("Error! You must enter a positive integer. Retype ");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Error! It isn't an integer. Retype:");
            }
        }
        return i;
    }

    //возвращает заполненный объект класcа doctor (без ID)
    private doctor CreateDoc() throws IOException, NotBoundException {
        Scanner sc = new Scanner(System.in);
        doctor doctor = new doctor();
        System.out.println("\nEnter name:");
        doctor.setName(sc.nextLine());
        System.out.println("\nEnter surname:");
        doctor.setSurname(sc.nextLine());
        System.out.println("\nEnter occupation:");
        doctor.setOccupation(sc.nextLine());
        System.out.println("\nEnter age:");
        int age = EnterAndCheck();
        while (age < 18) {
            System.out.println("\nAre you serious?!Plese enter correct age!:");
            age = EnterAndCheck();
        }
        doctor.setAge(age);
        return doctor;
    }

    //Добавление нового объекта на сервер
    private void AddDoc() throws Exception {
        doctor tmpDoc = CreateDoc();
        server.add(tmpDoc);
    }

    //получаем Index элемента по ID
    //Index==-1 - ID not found
    private int GetIndex(int ID) {
        int Index = -1;
        for (int m = 0; m < this.doctors.size(); m++)
            if (this.doctors.get(m).getID() == ID) {
                Index = m;
                break;
            }
        return Index;
    }

    private void ClearAll() throws IOException, NotBoundException {
        boolean status = server.delAll();
        if (status) {
            System.out.println("Success, press Enter");
        } else
            System.out.println("List is already empty");
        System.in.read();
    }

    private void DelOne() throws Exception {
        if (this.doctors.isEmpty())
            ErrorEmptyList();
        else {
            Scanner sc = new Scanner(System.in);
            System.out.println("\nEnter the ID to delete");
            int ID = EnterAndCheck();
            //удаляем объект из списка на сервере
            if (server.delElement(ID))
                System.out.println("Success, press Enter");
            else
                System.out.println("Doctor is not found, press Enter");
            System.in.read();
        }
    }

    private void EdInfo() throws Exception {
        if (this.doctors.isEmpty())
            ErrorEmptyList();
        else {
            System.out.println("Enter the ID to change:");
            int ID = EnterAndCheck();

            //проверяем существует ли доктор в текущей версии списка на клиенте
            int Index = GetIndex(ID);
            if (Index != -1) {
                System.out.println("\nEnter the new doctor:");
                doctor tmpDoc = CreateDoc();
                tmpDoc.setID(ID);
                //редактируем объект на сервере, если он еще существует
                if (this.server.edit(ID, tmpDoc))
                    System.out.println("Success, press Enter");
                else
                    System.out.println("Error: this doctor was deleted, press Enter");
            } else System.out.println("Not found, press Enter");
            System.in.read();
        }
    }

    private void SearchDoc() throws IOException, NotBoundException {
        if (server.getAll().isEmpty())
            ErrorEmptyList();
        else {
            System.out.println("Search menu:");
            System.out.println("1 - Search by ID");
            System.out.println("2 - Search by name");
            System.out.println("3 - Search by surname");
            System.out.println("4 - Search by occupation");
            System.out.println("5 - Search by age");
            System.out.println("any key - Exit");
            System.out.print("\nEnter the menu: ");
            int key = EnterAndCheck();
            Scanner sc = new Scanner(System.in);
            ArrayList<doctor> list;
            switch (key) {
                case 1:
                    System.out.println("\nEnter ID:");
                    int ID = EnterAndCheck();
                    list = server.findByID(ID);
                    if (!list.isEmpty()) printList(list);
                    else {
                        System.out.println("\nNot found, press Enter");
                        System.in.read();
                    }
                    break;
                case 2:
                    System.out.println("\nEnter name:");
                    String name = sc.nextLine();
                    list = server.findByName(name);
                    if (!list.isEmpty()) printList(list);
                    else {
                        System.out.println("\nNot found, press Enter");
                        System.in.read();
                    }
                    break;
                case 3:
                    System.out.println("\nEnter surname:");
                    String surname = sc.nextLine();
                    list = server.findBySurname(surname);
                    if (!list.isEmpty()) printList(list);
                    else {
                        System.out.println("\nNot found, press Enter");
                        System.in.read();
                    }
                    break;
                case 4:
                    System.out.println("\nEnter occupation:");
                    String occ = sc.nextLine();
                    list = server.findByOccupation(occ);
                    if (!list.isEmpty()) printList(list);
                    else {
                        System.out.println("\nNot found, press Enter");
                        System.in.read();
                    }
                    break;
                case 5:
                    System.out.println("\nEnter age:");
                    int age = EnterAndCheck();
                    list = server.findByAge(age);
                    if (!list.isEmpty()) printList(list);
                    else {
                        System.out.println("\nNot found, press Enter");
                        System.in.read();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}