package com.example.projet.controllers;

import com.example.projet.models.Offre;
import com.example.projet.models.Stagiaire;
import com.example.projet.services.OffreService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OffresController {
    @FXML private VBox offresContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterDomaine;
    @FXML private ComboBox<String> filterType;

    private Stagiaire stagiaireConnecte;
    private final OffreService offreService = new OffreService();
    private MesCandidaturesController mesCandidaturesController;

    @FXML
    public void initialize() {
        configureUI();
        loadData();
        logCurrentState();
    }

    private void configureUI() {
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        offresContainer.setSpacing(15);
        offresContainer.setPadding(new Insets(15));
        configureFilters();
        setupSearch();
    }

    private void loadData() {
        loadOffres();
    }

    private void logCurrentState() {
        System.out.println("Initialisation OffresController - Stagiaire: " +
                (stagiaireConnecte != null ? stagiaireConnecte.getEmail() : "null"));
    }

    private void configureFilters() {
        try {
            List<String> domaines = offreService.getAllDomaines();
            List<String> types = offreService.getAllTypes();

            Platform.runLater(() -> {
                filterDomaine.getItems().setAll("Tous les domaines");
                filterDomaine.getItems().addAll(domaines);

                filterType.getItems().setAll("Tous les types");
                filterType.getItems().addAll(types);

                filterDomaine.getSelectionModel().selectFirst();
                filterType.getSelectionModel().selectFirst();
            });

        } catch (SQLException e) {
            handleError("Erreur de configuration des filtres", e);
        }
    }

    private void loadOffres() {
        try {
            List<Offre> offres = offreService.getAllOffres();
            Platform.runLater(() -> updateOffresDisplay(offres));
        } catch (SQLException e) {
            handleError("Erreur de chargement des offres", e);
        }
    }

    private void updateOffresDisplay(List<Offre> offres) {
        offresContainer.getChildren().clear();

        if (offres.isEmpty()) {
            offresContainer.getChildren().add(createEmptyLabel("Aucune offre disponible"));
            return;
        }

        for (Offre offre : offres) {
            offresContainer.getChildren().add(createOffreCard(offre));
        }
    }

    private Label createEmptyLabel(String message) {
        Label label = new Label(message);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        return label;
    }

    private VBox createOffreCard(Offre offre) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        card.getChildren().addAll(
                createHeader(offre),
                createTitleLabel(offre),
                createDescriptionArea(offre),
                createFooter(offre),
                createActionButtons(offre)
        );
        return card;
    }

    private HBox createHeader(Offre offre) {
        HBox header = new HBox(10);
        VBox entrepriseInfo = new VBox(5);

        Label nomEntreprise = new Label(offre.getEntreprise().getNom());
        nomEntreprise.setStyle("-fx-font-weight: bold;");

        Label secteur = new Label(offre.getEntreprise().getSecteurActivite());
        secteur.setStyle("-fx-text-fill: #666;");

        entrepriseInfo.getChildren().addAll(nomEntreprise, secteur);
        header.getChildren().add(entrepriseInfo);

        return header;
    }

    private Label createTitleLabel(Offre offre) {
        Label titre = new Label(offre.getTitre());
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        return titre;
    }

    private TextArea createDescriptionArea(Offre offre) {
        TextArea description = new TextArea(offre.getDescription());
        description.setEditable(false);
        description.setWrapText(true);
        description.setMaxHeight(60);
        description.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        return description;
    }

    private HBox createFooter(Offre offre) {
        HBox footer = new HBox(20);
        footer.setPadding(new Insets(10, 0, 0, 0));
        footer.getChildren().addAll(
                new Label("⏱ " + offre.getDureeFormatee()),
                new Label(offre.getRemunerationFormatee()),
                new Label(offre.getDatePublicationFormatee())
        );
        return footer;
    }

    private HBox createActionButtons(Offre offre) {
        HBox buttons = new HBox(10);
        buttons.getChildren().addAll(
                createPostulerButton(offre),
                createFavoriButton(offre)
        );
        return buttons;
    }

    private Button createPostulerButton(Offre offre) {
        Button btn = new Button("Postuler");
        btn.setStyle("-fx-background-color: #0a66c2; -fx-text-fill: white;");
        btn.setOnAction(e -> handlePostuler(offre));
        return btn;
    }

    private Button createFavoriButton(Offre offre) {
        Button btn = new Button(offre.isFavori() ? "★" : "☆");
        btn.setStyle(offre.isFavori() ? "-fx-text-fill: gold;" : "-fx-text-fill: gray;");
        btn.setOnAction(e -> toggleFavori(offre, btn));
        return btn;
    }

    private void toggleFavori(Offre offre, Button btn) {
        offre.setFavori(!offre.isFavori());
        btn.setText(offre.isFavori() ? "★" : "☆");
        btn.setStyle(offre.isFavori() ? "-fx-text-fill: gold;" : "-fx-text-fill: gray;");
    }

    private void handlePostuler(Offre offre) {
        if (!validatePostulation(offre)) {
            return;
        }

        try {
            openCandidatureWindow(offre);
        } catch (IOException e) {
            handleError("Erreur d'ouverture du formulaire", e);
        }
    }

    private boolean validatePostulation(Offre offre) {
        if (offre == null) {
            showAlert("Erreur", "Aucune offre sélectionnée");
            return false;
        }

        if (stagiaireConnecte == null) {
            showAlertAndRedirect("Erreur", "Session expirée - Veuillez vous reconnecter");
            return false;
        }

        return true;
    }

    private void openCandidatureWindow(Offre offre) throws IOException {
        // Charger le contrôleur des candidatures si nécessaire


        // Charger la vue de candidature
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/CandidatureView.fxml"));
        Parent root = loader.load();

        // Configurer le contrôleur
        CandidatureController controller = loader.getController();
        controller.setOffre(offre);
        controller.setStagiaire(stagiaireConnecte);
        controller.setRefreshCallback(this::refreshMesCandidatures);

        // Afficher la fenêtre
        Stage stage = new Stage();
        stage.setTitle("Postuler à " + offre.getTitre());
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void loadMesCandidaturesController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/MesCandidaturesView.fxml"));
        Parent root = loader.load();
        mesCandidaturesController = loader.getController();
        mesCandidaturesController.loadCandidatures();
    }

    private void refreshMesCandidatures() {
        if (mesCandidaturesController != null) {
            mesCandidaturesController.refreshCandidatures();
        }
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                filterOffres();
            } catch (SQLException e) {
                handleError("Erreur de recherche", e);
            }
        });
    }

    @FXML
    private void handleFilter() {
        try {
            filterOffres();
        } catch (SQLException e) {
            handleError("Erreur de filtrage", e);
        }
    }

    private void filterOffres() throws SQLException {
        String domaine = filterDomaine.getValue();
        String type = filterType.getValue();
        String searchText = searchField.getText().toLowerCase();

        List<Offre> offresFiltrees = offreService.getOffresFiltrees(
                domaine.equals("Tous les domaines") ? null : domaine,
                type.equals("Tous les types") ? null : type
        );

        // Filtrage supplémentaire par texte
        if (!searchText.isEmpty()) {
            offresFiltrees.removeIf(offre ->
                    !offre.getTitre().toLowerCase().contains(searchText) &&
                            !offre.getDescription().toLowerCase().contains(searchText)
            );
        }

        Platform.runLater(() -> updateOffresDisplay(offresFiltrees));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertAndRedirect(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) offresContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            handleError("Erreur de redirection", e);
        }
    }

    private void handleError(String context, Exception e) {
        System.err.println(context + ": " + e.getMessage());
        Platform.runLater(() ->
                showAlert("Erreur", context + ": " + e.getMessage()));
    }

    public void setStagiaireConnecte(Stagiaire stagiaire) {
        this.stagiaireConnecte = stagiaire;
        System.out.println("OffresController - Stagiaire défini: " +
                (stagiaire != null ? stagiaire.getEmail() : "null"));
    }
}