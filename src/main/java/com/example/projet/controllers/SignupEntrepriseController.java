package com.example.projet.controllers;

import com.example.projet.models.Entreprise;
import com.example.projet.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

public class SignupEntrepriseController {

    @FXML private TextField nomEntrepriseField;
    @FXML private TextField secteurField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextArea descriptionField;
    @FXML private HBox rootPane;

    private AuthService authService;

    public SignupEntrepriseController() {
        this.authService = new AuthService();
    }

    // Vérification du format de l'email avec une expression régulière
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Hashage du mot de passe
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fonction d'inscription
    public void handleSignup() {
        String nomEntreprise = nomEntrepriseField.getText();
        String secteur = secteurField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String description = descriptionField.getText();

        // Vérification des champs obligatoires
        if (nomEntreprise.isEmpty() || secteur.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("❌ Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Vérification de l'email
        if (!isValidEmail(email)) {
            showAlert("❌ L'adresse email est invalide.");
            return;
        }

        // Création de l'objet Entreprise avec le mot de passe haché
        Entreprise entreprise = new Entreprise(nomEntreprise, secteur, email, password, description);

        // Appel au service d'authentification pour l'enregistrement
        boolean isSuccess = authService.registerEntreprise(entreprise);

        if (isSuccess) {
            showAlert("✅ Inscription réussie ! Redirection vers la page de connexion...");

            // Redirection vers la page de connexion
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/login.fxml"));
                Parent loginView = loader.load();
                rootPane.getScene().setRoot(loginView);
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
        alert.setTitle("Inscription Entreprise");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
