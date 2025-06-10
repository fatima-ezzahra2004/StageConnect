package com.example.projet.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML
    private ImageView backgroundImage; // Doit exister dans home.fxml avec fx:id="backgroundImage"

    private List<Image> images;
    private int currentIndex = 0;

    public void initialize() {
        images = new ArrayList<>();
        images.add(new Image("/com/example/projet/images/fond1.jpg"));
        images.add(new Image("/com/example/projet/images/fond2.jpg"));
        images.add(new Image("/com/example/projet/images/fond3.jpg"));

        startSlideshow();
    }

    private void startSlideshow() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> switchImage()),
                new KeyFrame(Duration.seconds(10))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void switchImage() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), backgroundImage);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            currentIndex = (currentIndex + 1) % images.size();
            backgroundImage.setImage(images.get(currentIndex));

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), backgroundImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // Navigation
    public void goToLogin(ActionEvent event) throws IOException {
        changeScene(event, "login.fxml");
    }

    public void goToSignupStagiaire(ActionEvent event) throws IOException {
        changeScene(event, "signup_stagiaire.fxml");
    }

    public void goToSignupEntreprise(ActionEvent event) throws IOException {
        changeScene(event, "signup_entreprise.fxml");
    }

    private void changeScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projet/" + fxmlFile));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
