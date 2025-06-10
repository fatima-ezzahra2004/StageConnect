package com.example.projet.controllers;

import com.example.projet.models.Candidature;
import com.example.projet.models.Stagiaire;
import com.example.projet.services.CandidatureService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class MesCandidaturesController {

    @FXML private TableView<Candidature> tableCandidatures;
    @FXML private TableColumn<Candidature, Integer> colId;
    @FXML private TableColumn<Candidature, String> colOffre;
    @FXML private TableColumn<Candidature, String> colDate;
    @FXML private TableColumn<Candidature, String> colStatut;

    private final CandidatureService candidatureService = new CandidatureService();
    private ObservableList<Candidature> candidatures = FXCollections.observableArrayList();

    private Stagiaire stagiaireConnecte;

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    public void setStagiaireConnecte(Stagiaire stagiaire) {
        this.stagiaireConnecte = stagiaire;
        loadCandidatures();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idCandidature"));
        colOffre.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(
                () -> cellData.getValue().getOffre() != null ? cellData.getValue().getOffre().getTitre() : ""
        ));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCandidature"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        tableCandidatures.setItems(candidatures);
    }

    public void loadCandidatures() {
        if (stagiaireConnecte != null) {
            candidatures.setAll(candidatureService.getCandidaturesByStagiaire(stagiaireConnecte.getIdStagiaire()));
        }
    }

    public void refreshCandidatures() {
        loadCandidatures();
    }
}
