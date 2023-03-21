package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {

    @FXML
    private ComboBox<String> standNameComboBox;

    private Tooltip tooltip = new Tooltip("Только числа");
    @FXML
    private ComboBox<String> userNameComboBox;

    @FXML
    private ComboBox<String> organizationComboBox;
    @FXML
    private TextField lettersAmountTextField;

    @FXML
    private ComboBox<String> topicComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    private final ObservableList<String> contractorStatuses = FXCollections.observableArrayList(
            "Created",
            "ReadyForSending",
            "Uploaded"
    );

    private final ObservableList<String> customerStatuses = FXCollections.observableArrayList(
            "Created",
            "Uploaded"
    );


    private final ObservableList<String> topics = FXCollections.observableArrayList(
            "Information Technology",
            "Quality Management",
            "Project Management",
            "Safety Management",
            "Radiation Safety",
            "Cost Management",
            "Human Management",
            "Change Management",
            "Interface Management",
            "Commissioning",
            "General",
            "Procurement and Supply",
            "Training",
            "Licensing and Permits",
            "Construction Management",
            "Security Management",
            "Contract Management",
            "Design Management"
    );
    @FXML
    private Label warningLabel;

    private BooleanProperty isInvalidInput = new SimpleBooleanProperty(false);


    @FXML
    private DatePicker letterDatePicker;

    private File chosenDirectory;

    @FXML
    public void initialize() {
        standNameComboBox.getItems().addAll("02", "04", "07", "11", "13", "d1", "t1");
        userNameComboBox.getItems().addAll("ec_user1", "migration_user", "oo_user1");
        organizationComboBox.getItems().addAll("JSC EC ASE", "JSC ASE", "Nuclear Power Plant Authority");
        topicComboBox.setItems(topics);
        warningLabel.visibleProperty().bind(isInvalidInput);
        organizationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if ("JSC EC ASE".equals(newValue) || "JSC ASE".equals(newValue)) {
                statusComboBox.setItems(contractorStatuses);
            } else if ("Nuclear Power Plant Authority".equals(newValue)) {
                statusComboBox.setItems(customerStatuses);
            } else {
                statusComboBox.setItems(FXCollections.emptyObservableList());
            }
        });
    }

    @FXML
    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = new Stage();
        chosenDirectory = directoryChooser.showDialog(stage);
    }

    @FXML
    private void saveToFile() {
        if (standNameComboBox.getValue() == null || userNameComboBox.getValue() == null || organizationComboBox.getValue() == null || letterDatePicker.getValue() == null) {
            showAlert("Выберите все параметры");
            return;
        }

        if (chosenDirectory == null) {
            showAlert("Выберите папку для сохранения");
            return;
        }

        int lettersAmount;
        try {
            lettersAmount = Integer.parseInt(lettersAmountTextField.getText());
        } catch (NumberFormatException e) {
            showAlert("Введите корректное количество писем (целое число)");
            return;
        }
        String policy;
        String project;

        String selectedOrganization = organizationComboBox.getValue();

        if ("JSC EC ASE".equals(selectedOrganization) || "JSC ASE".equals(selectedOrganization)) {
            policy = "IMS_PM_ContractorsLetter";
            project = "JSC EC ASE CS";
        } else if ("Nuclear Power Plant Authority".equals(selectedOrganization)) {
            policy = "IMS_PM_OwnersLetter";
            project = "Nuclear Power Plant Authority CS";
        } else {
            showAlert("Выберите корректную организацию");
            return;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");
        String formattedDate = letterDatePicker.getValue().atStartOfDay().format(dateFormatter);

        File outputFile = new File(chosenDirectory, "output_script.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("set context person creator;\n");
            writer.write("start  transaction;\n");

            for (int i = 1; i <= lettersAmount; i++) {
                String imsCode = generateImsCode(organizationComboBox.getValue(), topicComboBox.getValue(), i);
                String script = "add bus IMS_PM_Letter TestPmLetterFromJavaFX" + i + " 0 policy '" + policy + "' Description '999' IMS_Code '" + imsCode + "' Originated '"
                        + formattedDate + "' IMS_RegistrationNumber '322' Title 'titleTest"
                        + i + "' owner '" + userNameComboBox.getValue() + "' Originator '"
                        + userNameComboBox.getValue() + "' project '" + project + "' Organization '"
                        + organizationComboBox.getValue() + "' current 'ReadyForSending' IMS_CorrespondenceSubject 'subject text' IMS_RegistrationDate '"
                        + formattedDate + "';\n";
                writer.write(script);
            }

            writer.write("commit transaction;\n");

            showAlert("Файл успешно сохранен");
        } catch (IOException e) {
            showAlert("Ошибка при сохранении файла");
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleNumberInput(KeyEvent event) {
        if (!event.getCharacter().matches("\\d")) {
            event.consume();
            isInvalidInput.set(true);
            lettersAmountTextField.setOnKeyReleased(e -> isInvalidInput.set(false));
        }
    }
    private String generateImsCode(String organization, String topic, int letterNumber) {
        String organizationPart;
        String topicPart;
        String staticPart = "ED";
        String numberPart = String.format("-%06d", letterNumber);

        if ("JSC EC ASE".equals(organization) || "JSC ASE".equals(organization)) {
            organizationPart = "-ASEM-NPPA";
        } else {
            organizationPart = "-NPPA-ASEM";
        }

        switch (topic) {
            case "Information Technology":
                topicPart = "IT";
                break;
            case "Quality Management":
                topicPart = "QI";
                break;
            case "Project Management":
                topicPart = "PM";
                break;
            case "Safety Management":
                topicPart = "NS";
                break;
            case "Radiation Safety":
                topicPart = "RS";
                break;
            case "Cost Management":
                topicPart = "FI";
                break;
            case "Human Management":
                topicPart = "HR";
                break;
            case "Change Management":
                topicPart = "CM";
                break;
            case "Interface Management":
                topicPart = "IM";
                break;
            case "Commissioning":
                topicPart = "CO";
                break;
            case "General":
                topicPart = "GE";
                break;
            case "Procurement and Supply":
                topicPart = "PO";
                break;
            case "Training":
                topicPart = "TR";
                break;
            case "Licensing and Permits":
                topicPart = "LP";
                break;
            case "Construction Management":
                topicPart = "CA";
                break;
            case "Security Management":
                topicPart = "SM";
                break;
            case "Contract Management":
                topicPart = "LA";
                break;
            case "Design Management":
                topicPart = "DM";
                break;
            default:
                throw new IllegalArgumentException("Unknown topic: " + topic);
        }

        return staticPart + organizationPart + "-" + topicPart + numberPart;
    }

}

