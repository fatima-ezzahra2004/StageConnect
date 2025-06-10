package com.example.projet.controllers;

import com.example.projet.models.Offre;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class OffreEditDialogController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField domaineField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField niveauRequisField;
    @FXML private TextField dureeField;
    @FXML private DatePicker datePublicationPicker;
    @FXML private CheckBox estRemunereCheckBox;

    private Offre offre;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        typeComboBox.getItems().addAll("stage", "projet");
        datePublicationPicker.setValue(LocalDate.now());
    }

    public void setOffre(Offre offre) {
        this.offre = offre;

        titreField.setText(offre.getTitre());
        descriptionArea.setText(offre.getDescription());
        domaineField.setText(offre.getDomaine());
        typeComboBox.setValue(offre.getType());
        niveauRequisField.setText(offre.getNiveauRequis());
        dureeField.setText(offre.getDuree() > 0 ? String.valueOf(offre.getDuree()) : "");
        datePublicationPicker.setValue(offre.getDatePublication() != null ? offre.getDatePublication() : LocalDate.now());
        estRemunereCheckBox.setSelected(offre.isEstRemunere());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();

        if (titreField.getText() == null || titreField.getText().trim().isEmpty()) {
            errorMessage.append("Le titre est obligatoire.\n");
        }
        if (descriptionArea.getText() == null || descriptionArea.getText().trim().isEmpty()) {
            errorMessage.append("La description est obligatoire.\n");
        }
        if (domaineField.getText() == null || domaineField.getText().trim().isEmpty()) {
            errorMessage.append("Le domaine est obligatoire.\n");
        }
        if (typeComboBox.getValue() == null) {
            errorMessage.append("Le type est obligatoire.\n");
        }
        if (niveauRequisField.getText() == null || niveauRequisField.getText().trim().isEmpty()) {
            errorMessage.append("Le niveau requis est obligatoire.\n");
        }

        try {
            int duree = Integer.parseInt(dureeField.getText().trim());
            if (duree <= 0) {
                errorMessage.append("La durée doit être un entier positif.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("La durée doit être un nombre entier valide.\n");
        }

        if (datePublicationPicker.getValue() == null) {
            errorMessage.append("La date de publication est obligatoire.\n");
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Champs invalides", errorMessage.toString());
            return false;
        }

        return true;
    }

    private void updateOffre() {
        offre.setTitre(titreField.getText().trim());
        offre.setDescription(descriptionArea.getText().trim());
        offre.setDomaine(domaineField.getText().trim());
        offre.setType(typeComboBox.getValue());
        offre.setNiveauRequis(niveauRequisField.getText().trim());
        offre.setDuree(Integer.parseInt(dureeField.getText().trim()));
        offre.setDatePublication(datePublicationPicker.getValue());
        offre.setEstRemunere(estRemunereCheckBox.isSelected());
    }

    public void handleDialogResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            if (isInputValid()) {
                updateOffre();
                okClicked = true;
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}