package laba_1.GUI;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import laba_1.medServer;
import laba_1.model.doctor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by User on 11.05.2015.
 */
public class MainApp extends Application {

    //главное окно
    private Stage primaryStage;
    //обновляемый список объектов
    private ObservableList<doctor> doctors = FXCollections.observableArrayList();
    //удаленный интерфейс
    private medServer server;

    //check data's updates
    private SimpleBooleanProperty WasDataChanged = new SimpleBooleanProperty(false);

    public ObservableList<doctor> getDoctors() {
        return this.doctors;
    }
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public MainApp() throws Exception{
    }

    public SimpleBooleanProperty getWasDataChanged() {
        return this.WasDataChanged;
    }

    //существует ли данный id
    public boolean isIDexist (int ID){
        for (int k=0; k<doctors.size(); k++) {
            if (doctors.get(k).getID()==ID)
                return true;
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws RemoteException, NotBoundException{
        // соединение с реестром RMI
        Registry registry= LocateRegistry.getRegistry(1);
        // создание строки, содержащей URL удаленного объекта
        String objectName = "rmi://localhost/doctor";
        //  получение ссылки на удаленный объект
        server = (medServer) registry.lookup(objectName);
        //получение данных с сервера
        this.doctors.setAll(server.getAll());
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Doctors List");
        showDoctortable();
    }

    public void showDoctortable() throws RemoteException, NotBoundException{
        /*Предоставляем контроллеру доступ к главному классу
         и объекту-интерфейсу, для взаимодейстивия с сервером*/
        DoctorOverviewController controller = new DoctorOverviewController();
        controller.setMainAppComponents(this, this.server);

        //клиент становится сервером

        //получаем свободный порт от сервера
        int port = server.getFreePort();
        //порт, используемый сервером
        Registry registry = LocateRegistry.createRegistry(port);
        //имя клиента в RMI
        String MyClient = "rmi://localhost/doc";
        //связываем имя с классом-контроллером
        registry.rebind(MyClient, controller);
        //добавляем клиент в список клиентов на сервер
        this.server.addClient(MyClient);
    }

    //создает окно для ввода данных
    //возвращает true, если данные введены верно и нажата кнопка ОК
    public boolean showPersonEditDialog(doctor tmpDoctor) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit/Add Doctor");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        //передаем редактируемый/новый объект и созданное окно в контроллер
        PersonEditDialogController controller = new PersonEditDialogController(dialogStage, tmpDoctor, this);
        return controller.isOkClicked();
    }

    //Show search Stage
    public boolean showDoctorSearchDialog(ObservableList<doctor> FindedDocs) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Search Doctors");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        //добавляем список докторов и созданное окно в контроллер, даем ссылку на сервер
        SearchDialogController controller = new SearchDialogController(dialogStage, FindedDocs, server);
        //предоставляем контроллеру доступ к текущим данным
        return controller.isOkClicked();
    }

    public static void main(String[] args) {
        launch(args);
    }
}