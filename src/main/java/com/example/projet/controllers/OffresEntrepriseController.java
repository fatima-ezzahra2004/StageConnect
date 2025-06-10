package com.example.projet.controllers;

import com.example.projet.dao.OffreDAO;
import com.example.projet.models.Entreprise;
import com.example.projet.models.Offre;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OffresEntrepriseController {

    private Entreprise entreprise;

    @FXML private TableView<Offre> tableOffres;
    @FXML private TableColumn<Offre, String> colTitre;
    @FXML private TableColumn<Offre, String> colDescription;
    @FXML private TableColumn<Offre, String> colDomaine;
    @FXML private TableColumn<Offre, String> colType;
    @FXML private TableColumn<Offre, String> colNiveauRequis;
    @FXML private TableColumn<Offre, Integer> colDuree;
    @FXML private TableColumn<Offre, String> colDatePublication;
    @FXML private TableColumn<Offre, String> colRemuneration;

    private ObservableList<Offre> offresObservableList = FXCollections.observableArrayList();
    private OffreDAO offreDAO = new OffreDAO();

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
        loadOffres();
    }

    @FXML
    private void initialize() {
        // Configuration des colonnes
        colTitre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colDomaine.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDomaine()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        colNiveauRequis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNiveauRequis()));
        colDuree.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getDuree()).asObject());
        colDatePublication.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDatePublicationFormatee()));
        colRemuneration.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemunerationFormatee()));

        tableOffres.setItems(offresObservableList);
    }

    private void loadOffres() {
        try {
            List<Offre> offres = offreDAO.getOffresParEntreprise(entreprise.getIdEntreprise());
            offresObservableList.setAll(offres);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement des offres: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouterOffre() {
        Offre nouvelleOffre = new Offre();
        nouvelleOffre.setEntreprise(entreprise);

        boolean okClicked = showOffreEditDialog(nouvelleOffre);
        if (okClicked) {
            try {
                if (offreDAO.ajouterOffre(nouvelleOffre)) {
                    offresObservableList.add(nouvelleOffre);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre ajoutée avec succès.");
                    loadOffres(); // Recharger pour s'assurer de la synchronisation
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'ajout de l'offre a échoué.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'offre: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleModifierOffre() {
        Offre selected = tableOffres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une offre à modifier.");
            return;
        }

        // Création d'une copie pour éviter la modification directe
        Offre offreAModifier = new Offre(selected);

        boolean okClicked = showOffreEditDialog(offreAModifier);
        if (okClicked) {
            try {
                if (offreDAO.modifierOffre(offreAModifier)) {
                    // Mise à jour optimale de la table
                    int index = offresObservableList.indexOf(selected);
                    offresObservableList.set(index, offreAModifier);
                    tableOffres.refresh();
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre modifiée avec succès.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "La modification de l'offre a échoué.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSupprimerOffre() {
        Offre selected = tableOffres.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une offre à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette offre ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (offreDAO.supprimerOffre(selected.getIdMission())) {
                    offresObservableList.remove(selected);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Offre supprimée avec succès.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "La suppression de l'offre a échoué.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private boolean showOffreEditDialog(Offre offre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/OffreEditDialog.fxml"));
            DialogPane dialogPane = loader.load();
            OffreEditDialogController controller = loader.getController();
            controller.setOffre(offre);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(offre.getIdMission() == 0 ? "Nouvelle Offre" : "Modifier Offre");
            dialog.initModality(Modality.APPLICATION_MODAL);

            // Gestion du résultat du dialogue
            dialog.setResultConverter(buttonType -> {
                controller.handleDialogResult(buttonType);
                return buttonType;
            });

            Optional<ButtonType> result = dialog.showAndWait();
            return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE
                    && controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'édition: " + e.getMessage());
            return false;
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