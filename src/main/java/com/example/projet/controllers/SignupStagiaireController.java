package com.example.projet.controllers;

import com.example.projet.models.Stagiaire;
import com.example.projet.services.AuthService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

public class SignupStagiaireController {

    @FXML private TextField prenomField;
    @FXML private TextField nomField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextArea competencesField;
    @FXML private TextArea interetsField;

    private AuthService authService;

    public SignupStagiaireController() {
        // Initialisation du service d'authentification
        this.authService = new AuthService();
    }

    @FXML
    private javafx.scene.layout.HBox rootPane; // Racine de la scène

    // Validation de l'email avec une expression régulière
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Fonction d'inscription
    public void handleSignup() {
        String prenom = prenomField.getText();
        String nom = nomField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String competences = competencesField.getText();
        String interets = interetsField.getText();

        // Vérification des champs obligatoires
        if (prenom.isEmpty() || nom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("❌ Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Vérification de l'email
        if (!isValidEmail(email)) {
            showAlert("❌ L'adresse email est invalide.");
            return;
        }

        // Création du stagiaire avec mot de passe haché
        Stagiaire stagiaire = new Stagiaire(prenom, nom, email, password, competences,"", interets);

        // Appel au service d'authentification pour l'enregistrement
        boolean isSuccess = authService.registerStagiaire(stagiaire);

        if (isSuccess) {
            showAlert("✅ Inscription réussie ! Redirection vers la page de connexion...");

            // Redirection vers la page de connexion
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/projet/login.fxml"));
                javafx.scene.Parent loginView = loader.load();

                rootPane.getScene().setRoot(loginView); // Change la scène actuelle
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("❌ Erreur lors du chargement de la page de connexion.");
            }

        } else {
            showAlert("❌ Erreur lors de l'inscription. Veuillez réessayer.");
        }
    }

    // Méthode pour afficher une alerte
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inscription");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
