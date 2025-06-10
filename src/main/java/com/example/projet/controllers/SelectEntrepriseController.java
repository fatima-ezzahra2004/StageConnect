package com.example.projet.controllers;

import com.example.projet.models.Entreprise;
import com.example.projet.models.Stagiaire;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.function.Consumer;

public class SelectEntrepriseController {
    @FXML private ListView<Entreprise> entreprisesListView;

    private Stagiaire stagiaire;
    private Consumer<Entreprise> onEntrepriseSelected;

    // Ajoutez ces méthodes :
    public void setStagiaire(Stagiaire stagiaire) {
        this.stagiaire = stagiaire;
    }

    public void setOnEntrepriseSelected(Consumer<Entreprise> callback) {
        this.onEntrepriseSelected = callback;
    }

    public void chargerEntreprises(List<Entreprise> entreprises) {
        entreprisesListView.getItems().setAll(entreprises);
        entreprisesListView.setCellFactory(lv -> new ListCell<Entreprise>() {
            @Override
            protected void updateItem(Entreprise entreprise, boolean empty) {
                super.updateItem(entreprise, empty);
                setText(empty ? null : entreprise.getNom());
            }
        });
    }

    @FXML
    private void handleSelection() {
        Entreprise selected = entreprisesListView.getSelectionModel().getSelectedItem();
        if (selected != null && onEntrepriseSelected != null) {
            onEntrepriseSelected.accept(selected);
        }
    }
}