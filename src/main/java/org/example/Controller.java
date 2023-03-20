package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.FileWriter;
import java.io.IOException;

public class Controller {

    @FXML
    private ComboBox<String> nameComboBox;

    @FXML
    private ComboBox<String> surnameComboBox;

    @FXML
    private ComboBox<String> patronymicComboBox;

    @FXML
    public void initialize() {

        surnameComboBox.getItems().addAll("Смирнов", "Иванов", "Кузнецов");
        nameComboBox.getItems().addAll("Иван", "Петр", "Сергей");
        patronymicComboBox.getItems().addAll("Александрович", "Петрович", "Сергеевич");
    }

    @FXML
    private void saveToFile() {
        String name = nameComboBox.getValue();
        String surname = surnameComboBox.getValue();
        String patronymic = patronymicComboBox.getValue();

        if (name == null || surname == null || patronymic == null) {
            showAlert("Ошибка", "Пожалуйста, выберите имя, фамилию и отчество.");
            return;
        }

        String greeting = "Привет " + name + " " + surname + " " + patronymic;
        try (FileWriter writer = new FileWriter("greeting.txt")) {
            writer.write(greeting);
            showAlert("Успешно", "Текстовый файл успешно сохранен.");
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось сохранить файл.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
