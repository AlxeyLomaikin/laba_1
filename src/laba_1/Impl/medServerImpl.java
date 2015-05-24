package laba_1.Impl;

import laba_1.ClientUpd;
import laba_1.model.doctor;
import laba_1.medServer;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by User on 01.04.2015.
 */
public class medServerImpl extends UnicastRemoteObject implements medServer {
    //данные на сервере
    ArrayList<doctor> doctors;
    //список клиентов
    ArrayList<ClientUpd> Clients;
    int freeID;
    //свободный порт для нового клиента
    int freePort;

    @Override
    public int getFreePort() throws RemoteException {
        return this.freePort;
    }

    public medServerImpl() throws Exception {
        super();
        this.doctors = new ArrayList<doctor>();
        this.Clients = new ArrayList<ClientUpd>();
        this.freeID = 1;
        this.freePort = 2;
        this.ReadXML();
    }

    //добавляем клиента на сервер, передаем URL в RMI
    @Override
    public void addClient(String clientName) throws RemoteException, NotBoundException {
        //порт, используемый для взаимодействия сервера с клиентом
        Registry registry = LocateRegistry.getRegistry(freePort);
        freePort++;
        ClientUpd server = (ClientUpd) registry.lookup(clientName);
        Clients.add(server);
    }
    //Поиск по ID
    @Override
    public ArrayList<doctor> findByID(int ID) throws RemoteException {
        ArrayList<doctor> foundedDocs = new ArrayList<doctor>();
        for (doctor doctor : doctors) {
            if (doctor.getID() == ID) {
                foundedDocs.add(doctor);
                return foundedDocs;
            }
        }
        return foundedDocs;
    }

    //Поиск по возрасту
    @Override
    public ArrayList<doctor> findByAge(int age) throws RemoteException {
        ArrayList<doctor> foundedDocs = new ArrayList<doctor>();
        for (doctor doctor : doctors) {
            if (doctor.getAge() == age) {
                foundedDocs.add(doctor);
            }
        }
        return foundedDocs;
    }

    //Поиск по имени
    @Override
    public ArrayList<doctor> findByName(String name) throws RemoteException {
        ArrayList<doctor> foundedDocs = new ArrayList<doctor>();
        for (doctor doctor : doctors) {
            if (name.equals(doctor.getName())) {
                foundedDocs.add(doctor);
            }
        }
        return foundedDocs;
    }

    //Поиск по фамилии
    @Override
    public ArrayList<doctor> findBySurname(String surname) throws RemoteException {
        ArrayList<doctor> foundedDocs = new ArrayList<doctor>();
        for (doctor doctor : doctors) {
            if (surname.equals(doctor.getSurname())) {
                foundedDocs.add(doctor);
            }
        }
        return foundedDocs;
    }

    //Поиск по профессии
    @Override
    public ArrayList<doctor> findByOccupation(String occupation) throws RemoteException {
        ArrayList<doctor> foundedDocs = new ArrayList<doctor>();
        for (doctor doctor : doctors) {
            if (occupation.equals(doctor.getOccupation())) {
                foundedDocs.add(doctor);
            }
        }
        return foundedDocs;
    }

    //получение списка
    @Override
    public ArrayList<doctor> getAll() throws RemoteException {
        return this.doctors;
    }

    //получаем Index элемента по ID
    //Index==-1 - ID not found
    private int GetIndex (int ID)
    {
        int Index = -1;
        for (int m = 0; m<this.doctors.size(); m++)
            if(this.doctors.get(m).getID() == ID) {
                Index = m;
                break;
            }
        return Index;
    }

    //очистка данных на сервере
    @Override
    public boolean delAll() throws RemoteException {
        if (doctors.isEmpty()) return false;
        else {
            this.doctors.clear();
            //обновляем данные на всех клиентах
            for (int k = 0; k < Clients.size(); k++)
                Clients.get(k).Update();
            File docFile = new File("Doctors.xml");
            //удаляем файл
            if (docFile.exists() && docFile.length() != 0)
                docFile.delete();

            return true;
        }
    }

    //добавление объекта в список
    @Override
    public void add(doctor doctor) throws Exception {
        doctors.add(doctor);
        int Ind = doctors.size() - 1;
        //присваиваем ID новому объекту
        doctors.get(Ind).setID(freeID);
        freeID++;
        //изменяем XML
        addDoctorXML();
        //обновляем данные на всех клиентах
        for (int k = 0; k < Clients.size(); k++)
            Clients.get(k).Update();
    }

    //редактирование объекта
    @Override
    public boolean edit(int ID, doctor doctor) throws Exception {
        //получаем индекс по ID
        int Index = GetIndex(ID);
        if (Index!=-1) {
            doctors.set(Index, doctor);
            //изменяем XML
            editDoctorXML(doctor);
            //обновляем данные на всех клиентах
            for (int k = 0; k < Clients.size(); k++)
                Clients.get(k).Update();
            return true;
        }
        else
            return false;
    }

    @Override
    public ArrayList<doctor> findByAll(String id, String name, String surname,
                                       String occ, String age) throws RemoteException, NumberFormatException {
        //введеное поле не пусто
        boolean b1 = !((id == null) || (id.length() == 0));
        boolean b2 = !((name == null) || (name.length() == 0));
        boolean b3 = !((surname == null) || (surname.length() == 0));
        boolean b4 = !((occ == null) || (occ.length() == 0));
        boolean b5 = !((age == null) || (age.length() == 0));
        ArrayList<doctor>FoundDoctors = new ArrayList<>();
        FoundDoctors.addAll(doctors);
        //Поиск по id
        if (b1) {
            for (int k = 0; k < FoundDoctors.size(); k++) {
                if (FoundDoctors.get(k).getID() != Integer.parseInt(id)) {
                    FoundDoctors.remove(k);
                    k--;
                }
            }
        }
        //Поиск по имени
        if (b2) {
            for (int k = 0; k < FoundDoctors.size(); k++) {
                if (!FoundDoctors.get(k).getName().equals(name)) {
                    FoundDoctors.remove(k);
                    k--;
                }
            }
        }
        //Поиск по фамилии
        if (b3) {
            for (int k = 0; k < FoundDoctors.size(); k++) {
                if (!FoundDoctors.get(k).getSurname().equals(surname)) {
                    FoundDoctors.remove(k);
                    k--;
                }
            }
        }
        //Поиск по профессии
        if (b4) {
            for (int k = 0; k < FoundDoctors.size(); k++) {
                if (!(FoundDoctors.get(k).getOccupation().equals(occ))) {
                    FoundDoctors.remove(k);
                    k--;
                }
            }
        }
        //Поиск по возрасту
        if (b5) {
            for (int k = 0; k < FoundDoctors.size(); k++) {
                if (FoundDoctors.get(k).getAge() != Integer.parseInt(age)) {
                    FoundDoctors.remove(k);
                    k--;
                }

            }
        }
        return FoundDoctors;
    }

    //Удаление объекта
    @Override
    public boolean delElement(int ID) throws Exception {
        //получаем индекс по ID
        int Index = GetIndex(ID);
        if (Index!=-1) {
            doctors.remove(Index);
            //изменяем XML
            delDoctorXML(ID);
            //обновляем данные на всех клиентах
            for (int k = 0; k < Clients.size(); k++)
                Clients.get(k).Update();
            return true;
        }
       else
            return false;
    }

    //сохраняем DOM-дерево в файл
    private void WiriteDOMDoc(Document doc) throws TransformerException, RemoteException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("Doctors.xml"));
        //каждый тег с новой строки
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    /*добавление элементов в DOM-дерево
   doc - исходный документ
   startIndex - индекс объекта в списке,
    с которого нужно добавлять */
    private Document AddToDOM(Document doc, int startIndex) {
        Document updDoc = doc;
        Node root = doc.getFirstChild();
        for (int k = startIndex; k < doctors.size(); k++) {
            Element curDoctor = doc.createElement("doctor");
            root.appendChild(curDoctor);

            Attr attr = doc.createAttribute("id");
            attr.setValue(String.valueOf(doctors.get(k).getID()));
            curDoctor.setAttributeNode(attr);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(doctors.get(k).getName()));
            curDoctor.appendChild(name);

            Element surname = doc.createElement("surname");
            surname.appendChild(doc.createTextNode(doctors.get(k).getSurname()));
            curDoctor.appendChild(surname);

            Element occupation = doc.createElement("occupation");
            occupation.appendChild(doc.createTextNode(doctors.get(k).getOccupation()));
            curDoctor.appendChild(occupation);

            Element age = doc.createElement("age");
            age.appendChild(doc.createTextNode(String.valueOf(doctors.get(k).getAge())));
            curDoctor.appendChild(age);
        }
        return updDoc;
    }

    /*Создание xml из существующего списка
   Если файл существует - будет перезаписан*/
    private void CreateXML() throws IOException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("Doctors");
        doc.appendChild(root);
        doc = AddToDOM(doc, 0);
        WiriteDOMDoc(doc);
    }

    //Добавление элемента в файл
    private void addDoctorXML() throws Exception {
        File docFile = new File("Doctors.xml");

        //все ID, записанные в файле
        HashSet<Integer> FileID = new HashSet<Integer>();

         /*Valid==false - если файл "кривой":
        - есть одинаковые id
        - есть id<0
         */
        boolean Valid = true;
        //старая версия файла
        boolean OldFile = false;

        //Если файл существует и не пуст
        if (docFile.exists() && docFile.length() != 0) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(docFile);
            NodeList nList = doc.getElementsByTagName("doctor");

            //здесь получаем список ID в файле
            for (int k = 0; k < nList.getLength(); k++) {
                if (nList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nList.item(k);
                    int id = Integer.parseInt(el.getAttribute("id"));
                    //файл "кривой"
                    if (id < 0 | (!FileID.add(id))) {
                        Valid = false;
                        break;
                    }
                }
            }
            if (Valid) {
                //проверяем соответствуют ли ID в файле ID на сервере
                if (FileID.size() == doctors.size())
                    for (doctor doctor : doctors) {
                        OldFile = (!FileID.contains(doctor.getID()));
                        if (OldFile)
                            break;
                    }
                else OldFile = true;
                if (!OldFile) {
                    //индекс добавленного в список объекта
                    int startIndex = doctors.size()-1;
                    doc = AddToDOM(doc, startIndex);
                    WiriteDOMDoc(doc);
                }
                //если файл устарел - переписываем его
                else CreateXML();
            }
            ////если файл "кривой" - переписываем его
            else CreateXML();
        }
        //если файл пуст - просто создаем новый
        else CreateXML();
    }

    /*Удаление элемента из файла по ID
    Если файл "кривой" и в нем несколько одинаковых ID,
    то удалится первый найденный*/
    private void delDoctorXML(int ID) throws Exception {
        File docFile = new File("Doctors.xml");
        //найден ли ID в файле
        boolean success = false;
        if (docFile.exists() && docFile.length() != 0) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(docFile);
            NodeList nList = doc.getElementsByTagName("doctor");
            for (int k = 0; k < nList.getLength(); k++) {
                if (nList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nList.item(k);
                    if (Integer.parseInt(el.getAttribute("id")) == ID) {
                        nList.item(k).getParentNode().removeChild(nList.item(k));
                        success = true;
                        break;
                    }
                }
            }
            //Если элемент удален - переписываем файл
            if (success)
                WiriteDOMDoc(doc);
        }
    }

    /*Редактирование элемента в файле  по ID
      Если файл "кривой" и в нем несколько одинаковых ID,
      то будет изменен первый найденный*/
    private void editDoctorXML(doctor doctor) throws Exception {
        File docFile = new File("Doctors.xml");
        //найден ли ID в файле
        boolean success = false;
        if (docFile.exists() && docFile.length() != 0) {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(docFile);
            NodeList nList = doc.getElementsByTagName("doctor");
            for (int k = 0; k < nList.getLength(); k++) {
                if (nList.item(k).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nList.item(k);
                    if (Integer.parseInt(el.getAttribute("id")) == doctor.getID()) {
                        ((Element) nList.item(k)).getElementsByTagName("name").item(0).setTextContent(doctor.getName());
                        ((Element) nList.item(k)).getElementsByTagName("surname").item(0).setTextContent(doctor.getSurname());
                        ((Element) nList.item(k)).getElementsByTagName("occupation").item(0).setTextContent(doctor.getOccupation());
                        ((Element) nList.item(k)).getElementsByTagName("age").item(0).setTextContent(String.valueOf(doctor.getAge()));
                        success = true;
                        break;
                    }
                }
            }
            //Если элемент отредактирован - переписываем файл
            if (success)
                WiriteDOMDoc(doc);
        }
    }

    //чтение данных из файла
    private void ReadXML() throws Exception {
        File docFile = new File("Doctors.xml");
        if (docFile.exists() && docFile.length() != 0) {
            if (!doctors.isEmpty())
                this.delAll();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(docFile);
            NodeList nList = doc.getElementsByTagName("doctor");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nNode;
                    el.setAttribute("id", String.valueOf(temp));
                    String name = el.getElementsByTagName("name").item(0).getTextContent();
                    String surname = el.getElementsByTagName("surname").item(0).getTextContent();
                    String occupation = el.getElementsByTagName("occupation").item(0).getTextContent();
                    int age = Integer.parseInt(el.getElementsByTagName("age").item(0).getTextContent());
                    doctor doctor = new doctor(name, surname, occupation, age);
                    doctor.setID (temp+1);
                    doctors.add(doctor);
                    this.freeID = (temp + 2);
                }
                //переписываем файл для синхронизации с данными на сервере
                WiriteDOMDoc(doc);
            }
        }
    }
}
