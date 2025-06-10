package com.example.projet.controllers;

import com.example.projet.dao.MessageDAO;
import com.example.projet.models.Entreprise;
import com.example.projet.models.Message;
import com.example.projet.models.Stagiaire;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class MessagerieController {
    @FXML private ListView<Message> messagesListView;
    @FXML private TextArea messageTextArea;
    @FXML private Label contactLabel;

    private Entreprise entreprise;
    private Stagiaire stagiaire;
    private final MessageDAO messageDAO = new MessageDAO();
    private Timeline refreshTimeline;

    public void initialize() {
        // Configure la liste des messages avec ta fabrique de cellules personnalisée
        messagesListView.setCellFactory(lv -> new MessageListCell());

        // Rafraîchissement automatique toutes les 3 secondes
        refreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> refreshMessages())
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
        updateUI();
    }

    public void setStagiaire(Stagiaire stagiaire) {
        this.stagiaire = stagiaire;
        updateUI();
    }

    private void updateUI() {
        if (entreprise != null && stagiaire != null) {
            // Affiche le nom de l’entreprise (contact), pas celui du stagiaire connecté
            contactLabel.setText("Discussion avec " + entreprise.getNom());
            refreshMessages();
            refreshTimeline.play();
        }
    }

    private void refreshMessages() {
        try {
            List<Message> messages = messageDAO.getConversation(
                    stagiaire.getIdStagiaire(),
                    entreprise.getIdEntreprise()
            );

            messagesListView.getItems().setAll(messages);
            scrollToBottom();

            messageDAO.marquerMessagesLus(stagiaire.getIdStagiaire(), entreprise.getIdEntreprise());
        } catch (SQLException e) {
            showError("Erreur lors du chargement des messages");
            e.printStackTrace();
        }
    }

    private void scrollToBottom() {
        if (!messagesListView.getItems().isEmpty()) {
            messagesListView.scrollTo(messagesListView.getItems().size() - 1);
        }
    }

    @FXML
    private void handleEnvoyerMessage() {
        String texte = messageTextArea.getText().trim();
        if (texte.isEmpty()) return;

        Message message = createMessage(texte);

        try {
            if (messageDAO.ajouterMessage(message)) {
                messageTextArea.clear();
                refreshMessages();
            }
        } catch (SQLException e) {
            showError("Erreur lors de l'envoi du message");
            e.printStackTrace();
        }
    }

    private Message createMessage(String texte) {
        Message message = new Message();
        message.setContenu(texte);
        message.setDateEnvoi(LocalDateTime.now());

        // Ici le stagiaire est l'expéditeur (utilisateur connecté)
        message.setIdExpediteur(stagiaire.getIdStagiaire());

        // L'entreprise est le destinataire
        message.setIdDestinataire(entreprise.getIdEntreprise());

        // On peut aussi stocker l'idEntreprise dans le message si besoin pour filtrer
        message.setIdEntreprise(entreprise.getIdEntreprise());

        return message;
    }

    public void cleanup() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).show();
    }
}
