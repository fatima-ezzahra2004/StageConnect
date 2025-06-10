package com.example.projet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Chargement du fichier FXML principal (la page d'accueil ou dashboard)
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/projet/Home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);

        // Charger la feuille de style CSS pour le design de l'application
        scene.getStylesheets().add(getClass().getResource("/com/example/projet/css/dashboard.css").toExternalForm());

        // Configuration de la fenêtre principale
        stage.setTitle("Home");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        // Afficher la fenêtre principale
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
