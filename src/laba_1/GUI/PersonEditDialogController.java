package laba_1.GUI;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import laba_1.model.doctor;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by User on 12.05.2015.
 */
public class PersonEditDialogController {
    private TextField nameTF;
    private TextField surnameTF;
    private TextField occTF;
    private TextField ageTF;
    private Stage dialogStage;
    private boolean okClicked = false;

    private MainApp mainApp;

    private ChangeListener listener;

    public PersonEditDialogController(Stage dialogStage, doctor doctor, MainApp mainApp) {
        //получаем ссылку на окно редактирования
        this.dialogStage = dialogStage;
        this.mainApp = mainApp;
        initialize(doctor);
    }

    private void initialize(doctor tmpdoctor) {
        //расширяемая таблица с полями для ввода
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        nameTF = new TextField();
        surnameTF = new TextField();
        occTF = new TextField();
        ageTF = new TextField();
        //если объект редактируется показываем его текущее состояние
        if ((tmpdoctor.getName() != null)&&(tmpdoctor.getSurname()!= null)
                && (tmpdoctor.getOccupation() != null))
            fillTextFields(tmpdoctor);
        Label nameLabel = new Label("Name:");
        grid.add(nameLabel, 0, 0);
        grid.add(nameTF, 1, 0);
        Label surnameLabel = new Label("Surname:");
        grid.add(surnameLabel, 0, 1);
        grid.add(surnameTF, 1, 1);
        Label occLabel = new Label("Occupation:");
        grid.add(occLabel, 0, 2);
        grid.add(occTF, 1, 2);
        Label ageLabel = new Label("Age:");
        grid.add(ageLabel, 0, 3);
        grid.add(ageTF, 1, 3);

        Button OK = new Button("OK");
        OK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //если все введено правильно и ОК нажата
                if (isInputValid()) {
                    tmpdoctor.setName(nameTF.getText());
                    tmpdoctor.setSurname(surnameTF.getText());
                    tmpdoctor.setOccupation(occTF.getText());
                    tmpdoctor.setAge(Integer.parseInt(ageTF.getText()));
                    okClicked = true;
                    //delete listener when stage will be closing
                    mainApp.getWasDataChanged().removeListener(listener);
                    dialogStage.close();
                }
            }
        });
        //кнопка отмены
        Button Cancel = new Button("Cancel");
        Cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                okClicked = false;
                //delete listener when stage will be closing
                mainApp.getWasDataChanged().removeListener(listener);
                dialogStage.close();
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(OK, Cancel);
        hbox.setSpacing(5);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(grid, hbox);
        dialogStage.setScene(new Scene(vbox));
        //reset info about updates
        if (mainApp.getWasDataChanged().getValue())
            mainApp.getWasDataChanged().set(false);
        /*check for updates on server
        if we're editing doctor and he was deleted -
        close stage*/
        listener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if ((newValue)) {
                    /*ID != 0 - we're editing doctor;
                    and this ID isn't exist*/
                    if ( (tmpdoctor.getID() != 0)&&(!mainApp.isIDexist(tmpdoctor.getID()) ) )
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                okClicked = false;
                                //if stage is open
                                if (dialogStage.isShowing()) {
                                    dialogStage.close();
                                }
                                ArrayList<String>error = new ArrayList<String>();
                                error.add("Error: this doctor was deleted");
                                new ErrorMessageStage(error, mainApp.getPrimaryStage());
                                //delete listener when stage will be closing
                                mainApp.getWasDataChanged().removeListener(listener);
                            }
                        });
                    else mainApp.getWasDataChanged().set(false);
                }
            }
        };
        mainApp.getWasDataChanged().addListener(listener);
        //delete listener when stage will be closing
        dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                mainApp.getWasDataChanged().removeListener(listener);
            }
        });
        dialogStage.showAndWait();
    }

    //true, если OK нажата и данные введены верно
    public boolean isOkClicked() {
        return okClicked;
    }

    //показываем текущую информацию о редактируемом объекте
    private void fillTextFields (doctor tmpDoctor) {
        nameTF.setText(tmpDoctor.getName());
        surnameTF.setText(tmpDoctor.getSurname());
        occTF.setText(tmpDoctor.getOccupation());
        ageTF.setText(String.valueOf(tmpDoctor.getAge()));
    }

    //проверка на правильность ввода данных в окно
    private boolean isInputValid() {
        //список сообщение  возникших ошибках
        ArrayList<String> errorMessages = new ArrayList<>();
        if (nameTF.getText() == null || nameTF.getText().length() == 0) {
            errorMessages.add("No valid name!");
        }
        if (surnameTF.getText() == null || surnameTF.getText().length() == 0) {
            errorMessages.add("No valid surname!");
        }
        if (occTF.getText() == null || occTF.getText().length() == 0) {
            errorMessages.add("No valid occupation!");
        }

        if (ageTF.getText() == null || ageTF.getText().length() == 0) {
            errorMessages.add("No valid age!");
        } else {
            // если введено не число
            try {
                Integer.parseInt(ageTF.getText());
            } catch (NumberFormatException e) {
                errorMessages.add("Age must be integer!");
            }
        }
        if (errorMessages.size() == 0) {
            return true;
        } else {
            // выводим сообщение со списком ошибок
            new ErrorMessageStage(errorMessages, dialogStage);
            return false;
        }
    }
}
