package com.example.projet.controllers;

import com.example.projet.dao.CandidatureDAO;
import com.example.projet.models.Candidature;
import com.example.projet.models.Entreprise;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

public class CandidaturesRecuesController {

    private Entreprise entreprise;

    @FXML private TableView<Candidature> tableCandidatures;
    @FXML private TableColumn<Candidature, String> colNom;
    @FXML private TableColumn<Candidature, String> colPrenom;
    @FXML private TableColumn<Candidature, String> colEmail;
    @FXML private TableColumn<Candidature, String> colCv;
    @FXML private TableColumn<Candidature, String> colMission;
    @FXML private TableColumn<Candidature, String> colDate;
    @FXML private TableColumn<Candidature, String> colStatut;
    @FXML private TableColumn<Candidature, String> colModifierStatut;
    @FXML private Label notificationLabel;

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
        loadCandidatures();
    }

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStagiaire().getNom()));
        colPrenom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStagiaire().getPrenom()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStagiaire().getEmail()));
        colMission.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getOffre().getTitre()));
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateCandidature().toString()));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Bouton Télécharger CV
        colCv.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Télécharger");

            {
                setAlignment(Pos.CENTER);

                btn.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    String cvPath = candidature.getStagiaire().getCvPdf();

                    if (cvPath != null && !cvPath.isEmpty()) {
                        File sourceFile = new File(cvPath);
                        if (sourceFile.exists()) {
                            try {
                                File downloadsDir = new File(System.getProperty("user.home"), "Downloads");
                                if (!downloadsDir.exists()) downloadsDir.mkdirs();

                                String nomFichier = candidature.getStagiaire().getNom() + "_" +
                                        candidature.getStagiaire().getPrenom() + ".pdf";
                                File destFile = new File(downloadsDir, nomFichier);

                                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                showNotification("CV téléchargé avec succès !");
                            } catch (IOException e) {
                                showNotification("Erreur lors du téléchargement.");
                            }
                        } else {
                            showNotification("CV non trouvé.");
                        }
                    } else {
                        showNotification("Aucun CV disponible.");
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // ComboBox pour modifier le statut
        colModifierStatut.setCellFactory(param -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>();

            {
                comboBox.getItems().addAll("Acceptée", "Refusée");
                comboBox.setPrefWidth(110);
                setAlignment(Pos.CENTER);

                comboBox.setOnAction(event -> {
                    Candidature candidature = getTableView().getItems().get(getIndex());
                    String nouveauStatut = comboBox.getSelectionModel().getSelectedItem();

                    if (nouveauStatut != null && !nouveauStatut.isEmpty()) {
                        CandidatureDAO dao = new CandidatureDAO();
                        boolean success = dao.updateStatut(candidature.getIdCandidature(), nouveauStatut);

                        if (success) {
                            candidature.setStatut(nouveauStatut);
                            showNotification("Statut mis à jour : " + nouveauStatut);
                            getTableView().refresh();
                        } else {
                            showNotification("Erreur lors de la mise à jour.");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(comboBox);
                }
            }
        });
    }

    private void loadCandidatures() {
        CandidatureDAO dao = new CandidatureDAO();
        try {
            List<Candidature> candidatures = dao.getCandidaturesRecues(entreprise.getIdEntreprise());
            ObservableList<Candidature> data = FXCollections.observableArrayList(candidatures);
            tableCandidatures.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
            showNotification("Erreur chargement candidatures.");
        }
    }

    private void showNotification(String message) {
        Platform.runLater(() -> {
            notificationLabel.setText(message);
            notificationLabel.setVisible(true);

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {}
                Platform.runLater(() -> notificationLabel.setVisible(false));
            }).start();
        });
    }
}
