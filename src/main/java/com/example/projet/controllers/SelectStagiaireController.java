package com.example.projet.controllers;

import com.example.projet.models.Stagiaire;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.function.Consumer;

public class SelectStagiaireController {

    @FXML
    private ListView<Stagiaire> stagiairesListView;

    private Consumer<Stagiaire> onStagiaireSelected;

    // Pour charger la liste des stagiaires
    public void chargerStagiaires(List<Stagiaire> stagiaires) {
        stagiairesListView.getItems().setAll(stagiaires);

        // Personnaliser l'affichage des stagiaires dans la liste
        stagiairesListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Stagiaire stagiaire, boolean empty) {
                super.updateItem(stagiaire, empty);
                setText(empty || stagiaire == null ? null : stagiaire.getPrenom() + " " + stagiaire.getNom());
            }
        });
    }

    // Setter pour récupérer la fonction callback au moment de la sélection
    public void setOnStagiaireSelected(Consumer<Stagiaire> callback) {
        this.onStagiaireSelected = callback;
    }

    @FXML
    private void handleSelection() {
        Stagiaire selected = stagiairesListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un stagiaire avant de valider.");
            alert.showAndWait();
            return;
        }
        if (onStagiaireSelected != null) {
            onStagiaireSelected.accept(selected);
        }
    }
}
