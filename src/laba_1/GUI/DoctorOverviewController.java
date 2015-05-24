package laba_1.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import laba_1.ClientUpd;
import laba_1.medServer;
import laba_1.model.doctor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by User on 11.05.2015.
 */
public class DoctorOverviewController extends UnicastRemoteObject implements ClientUpd {

    private TableView doctorTable;
    private StackPane stackpane;
    private TableColumn IDCol;
    private TableColumn nameCol;
    private TableColumn surnameCol;
    private TableColumn occCol;
    private TableColumn ageCol;

    //link to MainApp
    private MainApp mainApp;
    //Link to remote object
    private medServer server;

    public DoctorOverviewController() throws RemoteException{
        super();
        initialize();
    }

    //update data on client when server's data will be changed
    public void Update () throws RemoteException{
        mainApp.getDoctors().setAll(server.getAll());
        mainApp.getWasDataChanged().set(true);
    }

    private void initialize() {
        this.stackpane = new StackPane();
        this.IDCol = new TableColumn();
        this.IDCol.setText("ID");
        this.IDCol.setCellValueFactory(new PropertyValueFactory("ID"));
        this.nameCol = new TableColumn();
        this.nameCol.setText("Name");
        this.nameCol.setCellValueFactory(new PropertyValueFactory("name"));
        this.occCol = new TableColumn();
        this.occCol.setText("Occupation");
        this.occCol.setCellValueFactory(new PropertyValueFactory("occupation"));
        this.surnameCol = new TableColumn();
        this.surnameCol.setText("Surname");
        this.surnameCol.setCellValueFactory(new PropertyValueFactory("surname"));
        this.ageCol = new TableColumn();
        this.ageCol.setText("Age");
        this.ageCol.setCellValueFactory(new PropertyValueFactory("age"));
        //Our Table with data
        this.doctorTable = new TableView();
        this.doctorTable.getColumns().addAll(IDCol, nameCol, surnameCol, occCol, ageCol);
        //TableColumns change their size when you change size of the window
        doctorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //delete selected doctor
        Button delete = new Button("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int selectedIndex = doctorTable.getSelectionModel().getSelectedIndex();
                //if doctor was selected
                if (selectedIndex != -1) {
                    try {
                        //id of selected doctor
                        int selectedID = mainApp.getDoctors().get(selectedIndex).getID();
                        //delete doctor from server
                        server.delElement(selectedID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //edit selected doctor
        Button edit = new Button("Edit");
        edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int selectedIndex = doctorTable.getSelectionModel().getSelectedIndex();
                //if doctor was sected
                if (selectedIndex != -1) {
                    //selected doctor and his id
                    doctor tmpDoc = mainApp.getDoctors().get(selectedIndex);
                    int selectedID = tmpDoc.getID();
                    boolean okClicked = mainApp.showPersonEditDialog(tmpDoc);
                    //if botton "Ok" was clicked
                    if (okClicked) {
                        try {
                            //edit doctor on server
                            server.edit(selectedID, tmpDoc);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //add new doctor
        Button add = new Button("New");
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //object for adding
                doctor tmpDoc = new doctor();
                boolean okClicked = mainApp.showPersonEditDialog(tmpDoc);
                //if botton "Ok" was clicked
                if (okClicked) {
                    try {
                        //add doctor on server
                        server.add(tmpDoc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //search doctor
        Button search = new Button("Search");
        search.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //if list with data isn't empty
                if (mainApp.getDoctors().size() != 0) {
                    //list of found doctors
                    ObservableList<doctor> FindDoctors = FXCollections.observableArrayList();
                    FindDoctors.setAll(mainApp.getDoctors());
                    boolean okClicked = mainApp.showDoctorSearchDialog(FindDoctors);
                    //if botton "Ok" was clicked
                    if (okClicked) {
                        //if list isn't empty
                        if (FindDoctors.size() == 0) {
                            ArrayList<String> Empty_list = new ArrayList<String>();
                            Empty_list.add("Not Found");
                            new ErrorMessageStage(Empty_list,  mainApp.getPrimaryStage());
                        } else {
                            ShowSearchRes(FindDoctors);
                        }
                    }
                }
            }
        });
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.getChildren().addAll(add, edit, delete, search);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(doctorTable, hbox);
        vbox.setSpacing(10);
        stackpane.getChildren().addAll(vbox);
    }

    //print result of search
    private void ShowSearchRes(ObservableList<doctor> FindDoctors) {
        Stage resStage = new Stage();
        resStage.setTitle("Search result");
        StackPane stackpane = new StackPane();
        TableView resTable = new TableView();
        resTable.getColumns().addAll(IDCol, nameCol, surnameCol, occCol, ageCol);
        resTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Button ok = new Button("OK");
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resStage.close();
            }
        });
        resTable.setItems(FindDoctors);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(resTable, ok);
        vbox.setSpacing(10);
        stackpane.getChildren().addAll(vbox);
        resStage.initOwner(mainApp.getPrimaryStage());
        resStage.setScene(new Scene(stackpane, 400, 400));
        resStage.show();
    }

    //get link to mainApp and some its components
    public void setMainAppComponents(MainApp mainApp, medServer server) {
        this.mainApp = mainApp;
        this.server = server;
        //fill our table
        doctorTable.setItems(mainApp.getDoctors());
        //show window with data
        mainApp.getPrimaryStage().setScene(new Scene(stackpane));
        mainApp.getPrimaryStage().show();
    }

}