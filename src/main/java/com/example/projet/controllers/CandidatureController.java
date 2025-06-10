package com.example.projet.controllers;

import com.example.projet.models.Candidature;
import com.example.projet.models.Offre;
import com.example.projet.models.Stagiaire;
import com.example.projet.services.CandidatureService;
import com.example.projet.services.StagiaireService;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;

public class CandidatureController {
    @FXML private Label lblOffreTitre;
    @FXML private Label lblStagiaireNom;
    @FXML private Button btnPostuler;

    private Offre offre;
    private Stagiaire stagiaire;
    private final CandidatureService candidatureService = new CandidatureService();
    private final StagiaireService stagiaireService = new StagiaireService();
    private Runnable refreshCallback;

    public void setOffre(Offre offre) {
        this.offre = offre;
        updateUI();
    }

    public void setStagiaire(Stagiaire stagiaire) {
        this.stagiaire = stagiaire;
        updateUI();
    }

    public void setRefreshCallback(Runnable refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    private void updateUI() {
        lblOffreTitre.setText(offre != null ? "Offre : " + offre.getTitre() : "Aucune offre sélectionnée");
        lblStagiaireNom.setText(stagiaire != null ?
                "Candidat : " + stagiaire.getNom() + " " + stagiaire.getPrenom() : "Non connecté");

        setupAnimations();
    }

    private void setupAnimations() {
        btnPostuler.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        btnPostuler.setOnMouseEntered(e ->
                btnPostuler.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;")
        );

        btnPostuler.setOnMouseExited(e ->
                btnPostuler.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;")
        );

        btnPostuler.setOnAction(e -> {
            btnPostuler.setDisable(true);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), btnPostuler);
            st.setFromX(1);
            st.setFromY(1);
            st.setToX(0.95);
            st.setToY(0.95);
            st.setAutoReverse(true);
            st.setCycleCount(2);

            st.setOnFinished(ev -> {
                Platform.runLater(() -> {
                    handlePostuler();
                    btnPostuler.setDisable(false);
                });
            });
            st.play();
        });
    }

    private void handlePostuler() {
        if (!isDataValid()) {
            return;
        }

        if (!hasValidCV()) {
            return;
        }

        submitApplication();
    }

    private boolean isDataValid() {
        if (stagiaire == null || stagiaire.getIdStagiaire() <= 0) {
            Platform.runLater(() -> showAlert("Erreur", "Vous devez être connecté", Alert.AlertType.ERROR));
            return false;
        }
        if (offre == null || offre.getIdMission() <= 0) {
            Platform.runLater(() -> showAlert("Erreur", "Aucune offre sélectionnée", Alert.AlertType.ERROR));
            return false;
        }
        return true;
    }

    private boolean hasValidCV() {
        if (stagiaire.getCvPdf() == null || stagiaire.getCvPdf().isEmpty()) {
            File cvFile = selectCVFile();
            if (cvFile == null) {
                Platform.runLater(() -> showAlert("Erreur", "Un CV est requis pour postuler", Alert.AlertType.ERROR));
                return false;
            }

            try {
                stagiaire.setCvPdf(cvFile.getAbsolutePath());
                stagiaireService.updateCvPath(stagiaire);
                return true;
            } catch (SQLException e) {
                Platform.runLater(() -> showAlert("Erreur", "Échec de l'enregistrement du CV", Alert.AlertType.ERROR));
                return false;
            }
        }
        return true;
    }

    private File selectCVFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionnez votre CV (PDF)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        Stage stage = (Stage) btnPostuler.getScene().getWindow();
        return fileChooser.showOpenDialog(stage);
    }

    private void submitApplication() {
        new Thread(() -> {
            try {
                Candidature candidature = new Candidature(
                        0, // ID généré par la base
                        LocalDate.now(),
                        "En attente",
                        stagiaire,
                        offre
                );

                candidatureService.ajouterCandidature(candidature);

                Platform.runLater(() -> {
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                    showAlert("Succès", "Candidature envoyée avec succès !", Alert.AlertType.INFORMATION);
                    redirectToDashboard();
                });
            } catch (SQLException e) {
                Platform.runLater(() ->
                        showAlert("Erreur", "Échec de l'envoi : " + e.getMessage(), Alert.AlertType.ERROR));
            }
        }).start();
    }

    private void redirectToDashboard() {
        try {
            // Fermer seulement la fenêtre de candidature
            Stage currentStage = (Stage) btnPostuler.getScene().getWindow();
            currentStage.close();

            // Si vous avez besoin de rafraîchir le dashboard existant:
            if (refreshCallback != null) {
                refreshCallback.run();
            }

        } catch (Exception e) {
            Platform.runLater(() ->
                    showAlert("Erreur", "Impossible de fermer la fenêtre: " + e.getMessage(), Alert.AlertType.ERROR));
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}