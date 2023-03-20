package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Controller {

    @FXML
    private ComboBox<String> nameComboBox;

    @FXML
    private ComboBox<String> surnameComboBox;

    @FXML
    private ComboBox<String> patronymicComboBox;

    private File chosenDirectory;

    @FXML
    public void initialize() {

        surnameComboBox.getItems().addAll("Смирнов", "Иванов", "Кузнецов");
        nameComboBox.getItems().addAll("Иван", "Петр", "Сергей");
        patronymicComboBox.getItems().addAll("Александрович", "Петрович", "Сергеевич");
    }

    @FXML
    public void saveToFile() {
        if (nameComboBox.getValue() == null || surnameComboBox.getValue() == null || patronymicComboBox.getValue() == null) {
            showAlert("Ошибка", "Необходимо выбрать имя, фамилию и отчество.");
            return;
        }

        if (chosenDirectory == null) {
            showAlert("Ошибка", "Необходимо выбрать папку для сохранения файла.");
            return;
        }

        String fileName = "greeting.txt";
        File file = new File(chosenDirectory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Привет " + nameComboBox.getValue() + " " + surnameComboBox.getValue() + " " + patronymicComboBox.getValue());
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось сохранить файл: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для сохранения файла");
        chosenDirectory = directoryChooser.showDialog(nameComboBox.getScene().getWindow());
    }
}
