package com.example.projet.controllers;

import com.example.projet.models.Entreprise;
import com.example.projet.services.OffreService;
import com.example.projet.services.CandidatureService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.util.Map;

public class StatistiquesController {

    @FXML
    private BarChart<String, Number> barChartOffres;

    @FXML
    private BarChart<String, Number> barChartCandidatures;

    private Entreprise entreprise;
    private OffreService offreService = new OffreService();
    private CandidatureService candidatureService = new CandidatureService();

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
        configurerAxes();
        chargerStatistiquesOffres();
        chargerStatistiquesCandidatures();
    }

    /**
     * Configuration des axes pour garantir des entiers à partir de 0.
     */
    private void configurerAxes() {
        // Configuration de l'axe des ordonnées pour le graphique des offres
        NumberAxis yAxisOffres = (NumberAxis) barChartOffres.getYAxis();
        yAxisOffres.setAutoRanging(false);
        yAxisOffres.setLowerBound(0);
        yAxisOffres.setTickUnit(1);
        yAxisOffres.setMinorTickVisible(false);

        // Configuration de l'axe des ordonnées pour le graphique des candidatures
        NumberAxis yAxisCandidatures = (NumberAxis) barChartCandidatures.getYAxis();
        yAxisCandidatures.setAutoRanging(false);
        yAxisCandidatures.setLowerBound(0);
        yAxisCandidatures.setTickUnit(1);
        yAxisCandidatures.setMinorTickVisible(false);
    }

    private void chargerStatistiquesOffres() {
        try {
            Map<String, Integer> statsOffres = offreService.getStatistiquesOffres(entreprise.getIdEntreprise());
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Offres");

            for (Map.Entry<String, Integer> entry : statsOffres.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChartOffres.getData().clear();
            barChartOffres.getData().add(series);

            ajusterHauteurAxe(barChartOffres);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerStatistiquesCandidatures() {
        try {
            Map<String, Integer> statsCandidatures = candidatureService.getStatistiquesCandidatures(entreprise.getIdEntreprise());
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Candidatures");

            for (Map.Entry<String, Integer> entry : statsCandidatures.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            barChartCandidatures.getData().clear();
            barChartCandidatures.getData().add(series);

            ajusterHauteurAxe(barChartCandidatures);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ajuste la hauteur de l'axe Y en fonction des données actuelles.
     */
    private void ajusterHauteurAxe(BarChart<String, Number> barChart) {
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        double max = barChart.getData().stream()
                .flatMap(series -> series.getData().stream())
                .mapToDouble(data -> data.getYValue().doubleValue())
                .max()
                .orElse(0);

        // Définir le `upperBound` pour que l'axe soit toujours un entier
        yAxis.setUpperBound(Math.ceil(max + 1));
    }
}
