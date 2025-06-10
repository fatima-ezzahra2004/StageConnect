package com.example.projet.controllers;

import com.example.projet.models.Message;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.time.format.DateTimeFormatter;

public class MessageListCell extends ListCell<Message> {
    private final TextFlow textFlow = new TextFlow();
    private final Text senderText = new Text();
    private final Text dateText = new Text();
    private final Text contentText = new Text();
    private final HBox container = new HBox();
    private final VBox messageBox = new VBox();

    public MessageListCell() {
        configureCellAppearance();
        setupCellStructure();
    }

    private void configureCellAppearance() {
        textFlow.setMaxWidth(300);
        contentText.setWrappingWidth(290);
        textFlow.getChildren().addAll(senderText, dateText, contentText);
    }

    private void setupCellStructure() {
        messageBox.getChildren().add(textFlow);
        container.getChildren().add(messageBox);
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setGraphic(null);
        } else {
            configureMessageAppearance(message);
            setGraphic(container);
        }
    }

    private void configureMessageAppearance(Message message) {
        String formattedDate = message.getDateEnvoi().format(DateTimeFormatter.ofPattern("dd/MM à HH:mm"));

        if (isFromCompany(message)) {
            styleAsCompanyMessage(formattedDate);
        } else {
            styleAsStudentMessage(formattedDate);
        }

        contentText.setText(message.getContenu());
    }

    private boolean isFromCompany(Message message) {
        return message.getIdExpediteur() != null;
    }

    private void styleAsCompanyMessage(String date) {
        senderText.setText("Entreprise (" + date + "): ");
        textFlow.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 5px; -fx-background-radius: 5px;");
        container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
    }

    private void styleAsStudentMessage(String date) {
        senderText.setText("Vous (" + date + "): ");
        textFlow.setStyle("-fx-background-color: #e8f5e9; -fx-padding: 5px; -fx-background-radius: 5px;");
        container.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
    }
}