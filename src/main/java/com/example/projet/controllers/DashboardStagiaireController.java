package com.example.projet.controllers;

import com.example.projet.dao.EntrepriseDAO;
import com.example.projet.models.Entreprise;
import com.example.projet.models.Stagiaire;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class DashboardStagiaireController {

    @FXML
    private StackPane contentPane;
    private Stagiaire stagiaireConnecte;
    private final EntrepriseDAO entrepriseDAO = new EntrepriseDAO();

    public void setStagiaireConnecte(Stagiaire stagiaire) {
        this.stagiaireConnecte = stagiaire;
        loadUI("ProfilView");
    }

    private void loadUI(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/" + fxml + ".fxml"));
            Node node = loader.load();

            // Configuration des contrôleurs en fonction de la vue chargée
            switch (fxml) {
                case "offres":
                    OffresController offresController = loader.getController();
                    offresController.setStagiaireConnecte(stagiaireConnecte);
                    break;
                case "MesCandidaturesView":
                    MesCandidaturesController mesCandidaturesController = loader.getController();
                    mesCandidaturesController.setStagiaireConnecte(stagiaireConnecte);
                    break;
                case "ProfilView":
                    ProfilController profilController = loader.getController();
                    profilController.setStagiaireConnecte(stagiaireConnecte);
                    break;
            }

            contentPane.getChildren().setAll(node);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la vue: " + fxml);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfil() {
        loadUI("ProfilView");
    }

    @FXML
    private void handleOffres() {
        loadUI("offres");
    }

    @FXML
    private void handleCandidatures() {
        loadUI("MesCandidaturesView");
    }

    @FXML
    private void handleMessagerie() {
        try {
            // On récupère toutes les entreprises (plus de filtre par stagiaire)
            List<Entreprise> entreprises = entrepriseDAO.getAllEntreprises();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/select_entreprise.fxml"));
            Parent root = loader.load();

            SelectEntrepriseController controller = loader.getController();
            controller.setStagiaire(stagiaireConnecte);
            controller.chargerEntreprises(entreprises);

            controller.setOnEntrepriseSelected(entreprise -> {
                try {
                    openMessagerieWithEntreprise(entreprise);
                } catch (IOException ex) {
                    showAlert("Erreur", "Impossible d'ouvrir la messagerie avec cette entreprise");
                    ex.printStackTrace();
                }
            });

            contentPane.getChildren().setAll(root);
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la sélection des entreprises");
            e.printStackTrace();
        }
    }

    private void openMessagerieWithEntreprise(Entreprise entreprise) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/messagerie.fxml"));
        Parent root = loader.load();

        MessagerieController controller = loader.getController();
        controller.setStagiaire(stagiaireConnecte);
        controller.setEntreprise(entreprise);

        Stage stage = new Stage();
        stage.setTitle("Messagerie avec " + entreprise.getNom());
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(e -> controller.cleanup());
        stage.show();
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) contentPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de se déconnecter");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
