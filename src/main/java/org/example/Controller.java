package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Controller {

    @FXML
    private ComboBox<String> standNameComboBox;

    @FXML
    private ComboBox<String> userNameComboBox;

    @FXML
    private ComboBox<String> patronymicComboBox;

    private File chosenDirectory;

    @FXML
    public void initialize() {
        standNameComboBox.getItems().addAll("02", "04", "07", "11", "13", "d1", "t1");
        userNameComboBox.getItems().addAll("ec_user1", "migration_user", "oo_user1");
        patronymicComboBox.getItems().addAll("Иванович", "Алексеевич", "Петрович");
    }

    @FXML
    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = new Stage();
        chosenDirectory = directoryChooser.showDialog(stage);
    }

    @FXML
    private void saveToFile() {
        if (standNameComboBox.getValue() != null && userNameComboBox.getValue() != null && patronymicComboBox.getValue() != null) {
            String content = "Привет, " + userNameComboBox.getValue() + " " + standNameComboBox.getValue() + " " + patronymicComboBox.getValue();
            String fileName = "letter.txt";

            if (chosenDirectory != null) {
                File file = new File(chosenDirectory, fileName);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Выберите папку для сохранения файла");
            }
        } else {
            System.out.println("Пожалуйста, выберите все значения");
        }
    }
}

