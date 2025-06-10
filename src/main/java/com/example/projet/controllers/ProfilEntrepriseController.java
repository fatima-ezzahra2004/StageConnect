package com.example.projet.controllers;

import com.example.projet.models.Entreprise;
import com.example.projet.services.EntrepriseService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ProfilEntrepriseController {

    private EntrepriseService entrepriseService = new EntrepriseService();

    @FXML
    private TextField nomField;
    @FXML
    private TextField secteurField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private PasswordField motDePasseField;
    @FXML
    public void initialize() {
        afficherEntreprise();
    }

    @FXML
    private void updateEntreprise(javafx.event.ActionEvent event) {
        String nom = nomField.getText();
        String secteur = secteurField.getText();
        String email = emailField.getText();
        String description = descriptionArea.getText();
        String motDePasse = motDePasseField.getText();

        Entreprise entreprise = new Entreprise();
        entreprise.setIdEntreprise(1);  // Remplacer par l'ID réel de l'entreprise
        entreprise.setNom(nom);
        entreprise.setSecteurActivite(secteur);
        entreprise.setEmail(email);
        entreprise.setDescription(description);
        entreprise.setMotDePasse(motDePasse);

        boolean updated = entrepriseService.updateEntreprise(entreprise);

        if (updated) {
            System.out.println("Mise à jour réussie !");
            // Par exemple afficher une alerte JavaFX
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Profil mis à jour avec succès.");
            alert.showAndWait();
        } else {
            System.out.println("Erreur lors de la mise à jour !");
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("La mise à jour a échoué.");
            alert.showAndWait();
        }
    }


    private void afficherEntreprise() {
        Entreprise entreprise = entrepriseService.getEntrepriseById(1);

        if (entreprise != null) {
            nomField.setText(entreprise.getNom());
            secteurField.setText(entreprise.getSecteurActivite());
            emailField.setText(entreprise.getEmail());
            descriptionArea.setText(entreprise.getDescription());
        } else {
            System.out.println("Aucune entreprise trouvée !");
        }
    }
    public void setEntreprise(Entreprise entreprise) {
        if (entreprise != null) {
            nomField.setText(entreprise.getNom());
            secteurField.setText(entreprise.getSecteurActivite());
            emailField.setText(entreprise.getEmail());
            descriptionArea.setText(entreprise.getDescription());
            motDePasseField.setText(entreprise.getMotDePasse());
        }
    }


}
