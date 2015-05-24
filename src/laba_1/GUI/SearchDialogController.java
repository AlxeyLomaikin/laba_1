package laba_1.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import laba_1.medServer;
import laba_1.model.doctor;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by User on 12.05.2015.
 */
public class SearchDialogController {

    private TextField idTF;
    private TextField nameTF;
    private TextField surnameTF;
    private TextField occTF;
    private TextField ageTF;
    private Stage dialogStage;
    private boolean okClicked = false;

//link to remote object
    private medServer server;

    public SearchDialogController(Stage dialogStage, ObservableList<doctor> FindedDoctors, medServer server) {
        //получаем ссылку на окно поиска
        this.dialogStage = dialogStage;
        this.server = server;
        initialize(FindedDoctors);
    }

    private void initialize(ObservableList<doctor> FindedDoctors) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        idTF = new TextField();
        nameTF = new TextField();
        surnameTF = new TextField();
        occTF = new TextField();
        ageTF = new TextField();
        Label idLabel = new Label("ID:");
        grid.add(idLabel, 0, 0);
        grid.add(idTF, 1, 0);
        Label nameLabel = new Label("Name:");
        grid.add(nameLabel, 0, 1);
        grid.add(nameTF, 1, 1);
        Label surnameLabel = new Label("Surname:");
        grid.add(surnameLabel, 0, 2);
        grid.add(surnameTF, 1, 2);
        Label occLabel = new Label("Occupation:");
        grid.add(occLabel, 0, 3);
        grid.add(occTF, 1, 3);
        Label ageLabel = new Label("Age:");
        grid.add(ageLabel, 0, 4);
        grid.add(ageTF, 1, 4);

        Button OK = new Button("OK");
        OK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //если все введено правильно и ОК нажата
                if (isInputValid()) {
                    try {
                        //search data on server
                        FindedDoctors.setAll(server.findByAll(idTF.getText(), nameTF.getText(), surnameTF.getText(),
                                occTF.getText(), ageTF.getText()));
                        okClicked = true;
                        dialogStage.close();
                    }catch(RemoteException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        Button Cancel = new Button("Cancel");
        Cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                okClicked = false;
                dialogStage.close();
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(OK, Cancel);
        hbox.setSpacing(5);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(grid, hbox);
        dialogStage.setScene(new Scene(vbox));
        dialogStage.showAndWait();
    }

    //true, если OK нажата и данные введены верно
    public boolean isOkClicked() {
        return okClicked;
    }

    //проверка на правильность ввода данных в окно
    private boolean isInputValid() {
        //список сообщение  возникших ошибках
        ArrayList<String> errorMessages = new ArrayList<>();
        boolean b1 = ((idTF.getText() == null) || (idTF.getText().length() == 0));
        boolean b2 = ((nameTF.getText() == null) || (nameTF.getText().length() == 0));
        boolean b3 = ((surnameTF.getText() == null) || (surnameTF.getText().length() == 0));
        boolean b4 = ((occTF.getText() == null) || (occTF.getText().length() == 0));
        boolean b5 = ((ageTF.getText() == null) || (ageTF.getText().length() == 0));
        //поля не заполнены
        if (b1 && b2 && b3 && b4 && b5)
            errorMessages.add("No entered data !");
            ///проверяем, чтобы в поля id и(или) age были введены числа
        else {
            if (!b5) {
                try {
                    Integer.parseInt(ageTF.getText());
                } catch (NumberFormatException e) {
                    errorMessages.add("Age must be integer!");
                }
            }
            if (!b1) {
                try {
                    Integer.parseInt(idTF.getText());
                } catch (NumberFormatException e) {
                    errorMessages.add("id must be integer!");
                }
            }
        }
        if (errorMessages.size() == 0) {
            return true;
        } else {
            // // выводим сообщение со списком ошибок
            new ErrorMessageStage(errorMessages, dialogStage);
            return false;
        }
    }
}
