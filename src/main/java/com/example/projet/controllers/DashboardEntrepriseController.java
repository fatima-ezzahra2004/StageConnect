package com.example.projet.controllers;

import com.example.projet.dao.StagiaireDAO;
import com.example.projet.models.Entreprise;
import com.example.projet.models.Stagiaire;
import com.example.projet.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class DashboardEntrepriseController {

    @FXML private StackPane contentArea;
    @FXML private Label lblNomEntreprise;
    @FXML private Label lblEmail;
    @FXML private Label lblSecteurActivite;

    private Entreprise entrepriseConnectee;
    private final StagiaireDAO stagiaireDAO = new StagiaireDAO();

    public void initialize() {
        this.entrepriseConnectee = SessionManager.getCurrentEntreprise();
        updateHeader();
        loadDefaultView();
    }

    private void updateHeader() {
        if (entrepriseConnectee != null) {
            lblNomEntreprise.setText(entrepriseConnectee.getNom());
            lblEmail.setText(entrepriseConnectee.getEmail());
            lblSecteurActivite.setText(entrepriseConnectee.getSecteurActivite());
        }
    }

    private void loadDefaultView() {
        loadView("/com/example/projet/offres_entreprise.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            try {
                Object controller = loader.getController();
                if (controller != null) {
                    controller.getClass().getMethod("setEntreprise", Entreprise.class)
                            .invoke(controller, entrepriseConnectee);
                }
            } catch (Exception e) {
                System.out.println("Méthode setEntreprise non trouvée dans " + fxmlPath);
            }

            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible de charger la vue : " + fxmlPath);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGestionOffres() {
        loadView("/com/example/projet/offres_entreprise.fxml");
    }

    @FXML
    private void handleCandidaturesRecues() {
        loadView("/com/example/projet/candidatures_recues.fxml");
    }

    @FXML
    private void handleProfilEntreprise() {
        loadView("/com/example/projet/profil_entreprise.fxml");
    }

    @FXML
    private void handleStatistiques() {
        loadView("/com/example/projet/statistiques.fxml");
    }

    @FXML
    private void handleMessagerie() {
        try {
            System.out.println("Entreprise connectée ID: " + entrepriseConnectee.getIdEntreprise());

            // Ici potentielle SQLException, on est dans un try donc c'est géré
            List<Stagiaire> stagiaires = stagiaireDAO.getStagiairesForEntreprise(entrepriseConnectee.getIdEntreprise());

            System.out.println("Nombre de stagiaires trouvés: " + stagiaires.size());
            stagiaires.forEach(s -> System.out.println(s.getPrenom() + " " + s.getNom()));

            if (stagiaires.isEmpty()) {
                showAlert("Information", "Aucun échange message existant avec des stagiaires");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/select_stagiaire.fxml"));
            Parent root = loader.load();

            SelectStagiaireController controller = loader.getController();
            controller.chargerStagiaires(stagiaires);

            controller.setOnStagiaireSelected(stagiaire -> openMessagerieWithStagiaire(stagiaire));

            contentArea.getChildren().setAll(root);

        } catch (IOException | SQLException e) {
            showError("Erreur Critique", "Échec du chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openMessagerieWithStagiaire(Stagiaire stagiaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/messagerie.fxml"));
            Parent root = loader.load();

            MessagerieController controller = loader.getController();
            controller.setEntreprise(this.entrepriseConnectee);
            controller.setStagiaire(stagiaire);

            contentArea.getChildren().setAll(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText("Êtes-vous sûr de vouloir vous déconnecter ?");
        confirmation.setContentText("Vous serez redirigé vers la page de connexion.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            SessionManager.logout();
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        try {
            Stage stage = (Stage) contentArea.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/login.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
        } catch (IOException e) {
            showError("Erreur critique", "Impossible de charger la vue de connexion");
            e.printStackTrace();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
