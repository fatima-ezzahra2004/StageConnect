package com.example.projet.controllers;

import com.example.projet.models.Entreprise;
import com.example.projet.models.Stagiaire;
import com.example.projet.services.AuthService;
import com.example.projet.utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML public TextField emailField;
    @FXML public PasswordField passwordField;
    @FXML public RadioButton rbStagiaire;
    @FXML public RadioButton rbEntreprise;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        rbStagiaire.setToggleGroup(group);
        rbEntreprise.setToggleGroup(group);
    }

    public void handleLogin(ActionEvent event) throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        if (rbStagiaire.isSelected()) {
            handleStagiaireLogin(event, email, password);
        } else if (rbEntreprise.isSelected()) {
            handleEntrepriseLogin(event, email, password);
        } else {
            showAlert("Veuillez sélectionner un type d'utilisateur.");
        }
    }

    private void handleStagiaireLogin(ActionEvent event, String email, String password) throws IOException {
        Stagiaire stagiaire = authService.loginStagiaire(email, password);
        if (stagiaire != null) {
            SessionManager.loginStagiaire(stagiaire);
            redirectToDashboardStagiaire(event, "dashboard_stagiaire.fxml", stagiaire);
        } else {
            showAlert("Échec de connexion pour le stagiaire");
        }
    }

    private void handleEntrepriseLogin(ActionEvent event, String email, String password) throws IOException {
        Entreprise entreprise = authService.loginEntreprise(email, password);
        if (entreprise != null) {
            SessionManager.loginEntreprise(entreprise);
            redirectToDashboardEntreprise(event, "dashboard_entreprise.fxml");
        } else {
            showAlert("Informations de connexion incorrectes pour l'entreprise.");
        }
    }

    private void redirectToDashboardStagiaire(ActionEvent event, String fxml, Stagiaire stagiaire) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/" + fxml));
        Parent root = loader.load();

        if (stagiaire != null) {
            DashboardStagiaireController controller = loader.getController();
            controller.setStagiaireConnecte(stagiaire);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void redirectToDashboardEntreprise(ActionEvent event, String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/" + fxml));
        Parent root = loader.load();

        // Pas besoin de passer l'entreprise ici, elle est dans SessionManager

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void goToSignup(ActionEvent event) {
        try {
            String fxmlFile = rbStagiaire.isSelected()
                    ? "/com/example/projet/signup_stagiaire.fxml"
                    : "/com/example/projet/signup_entreprise.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur de navigation");
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Erreur de connexion");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
