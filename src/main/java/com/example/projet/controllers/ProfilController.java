package com.example.projet.controllers;

import com.example.projet.dao.StagiaireDAO;
import com.example.projet.models.Stagiaire;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;

public class ProfilController {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;

    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField pfMotDePasse;

    @FXML
    private TextArea taCompetences;

    @FXML
    private TextArea taInterets;

    @FXML
    private Button btnModifierCV;

    private Stagiaire stagiaireConnecte;
    private File fichierCVSelectionne = null;

    public void setStagiaireConnecte(Stagiaire stagiaire) {
        this.stagiaireConnecte = stagiaire;
        afficherInfosStagiaire();
    }

    private void afficherInfosStagiaire() {
        if (stagiaireConnecte != null) {
            tfNom.setText(stagiaireConnecte.getNom());
            tfPrenom.setText(stagiaireConnecte.getPrenom());
            tfEmail.setText(stagiaireConnecte.getEmail());
            pfMotDePasse.setText(stagiaireConnecte.getMotDePasse());
            taCompetences.setText(stagiaireConnecte.getCompetences());
            taInterets.setText(stagiaireConnecte.getInterets());
        }
    }

    @FXML
    private void handleModifierCV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier CV (PDF)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));
        File fichier = fileChooser.showOpenDialog(btnModifierCV.getScene().getWindow());

        if (fichier != null) {
            fichierCVSelectionne = fichier;  // on stocke le fichier choisi
            System.out.println("CV sélectionné : " + fichier.getAbsolutePath());
        }
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleEnregistrer() {
        if (stagiaireConnecte != null) {
            try {
                // Valider les entrées
                if (tfNom.getText().isEmpty() || tfPrenom.getText().isEmpty() || tfEmail.getText().isEmpty()) {
                    showAlert("Erreur", "Les champs Nom, Prénom et Email sont obligatoires", Alert.AlertType.ERROR);
                    return;
                }

                // Mettre à jour l'objet stagiaire
                stagiaireConnecte.setNom(tfNom.getText());
                stagiaireConnecte.setPrenom(tfPrenom.getText());
                stagiaireConnecte.setEmail(tfEmail.getText());
                stagiaireConnecte.setMotDePasse(pfMotDePasse.getText());
                stagiaireConnecte.setCompetences(taCompetences.getText());
                stagiaireConnecte.setInterets(taInterets.getText());

                StagiaireDAO dao = new StagiaireDAO();

                // Mettre à jour les infos générales
                dao.updateStagiaire(stagiaireConnecte);

                // Mettre à jour le CV si nécessaire
                if (fichierCVSelectionne != null) {
                    dao.updateCvPath(stagiaireConnecte.getIdStagiaire(), fichierCVSelectionne.getAbsolutePath());
                    stagiaireConnecte.setCvPdf(fichierCVSelectionne.getAbsolutePath());
                    fichierCVSelectionne = null;
                }

                showAlert("Succès", "Profil mis à jour avec succès", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur SQL", "Erreur lors de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Une erreur inattendue est survenue", Alert.AlertType.ERROR);
            }
        }
    }


    @FXML
    private void handleAnnuler() {
        afficherInfosStagiaire();
    }
}