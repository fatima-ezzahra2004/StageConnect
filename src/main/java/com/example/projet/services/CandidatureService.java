package com.example.projet.services;

import com.example.projet.dao.CandidatureDAO;
import com.example.projet.models.Candidature;
import com.example.projet.models.Offre;
import com.example.projet.models.Stagiaire;
import com.example.projet.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidatureService {

    private final CandidatureDAO candidatureDAO = new CandidatureDAO();

    /**
     * Ajouter une candidature
     */
    public void ajouterCandidature(Candidature candidature) throws SQLException {
        if (candidature.getStagiaire() == null || candidature.getOffre() == null) {
            throw new IllegalArgumentException("Stagiaire et Offre doivent être spécifiés");
        }

        candidatureDAO.ajouterCandidature(candidature);
    }

    /**
     * Récupérer les candidatures par ID de stagiaire
     */
    public List<Candidature> getCandidaturesByStagiaire(int stagiaireId) {
        try {
            return candidatureDAO.getCandidaturesByStagiaireId(stagiaireId);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des candidatures", e);
        }
    }

    /**
     * Récupérer les statistiques de candidatures par mois pour une entreprise
     */
    public Map<String, Integer> getStatistiquesCandidatures(int entrepriseId) throws SQLException {
        String query = """
                SELECT MONTH(c.date_candidature) AS mois, COUNT(*) AS nombre
                FROM candidature c 
                JOIN mission m ON c.mission_id = m.idMission 
                WHERE m.entreprise_id = ? 
                GROUP BY mois ORDER BY mois
                """;

        Map<String, Integer> statistiques = new HashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, entrepriseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int mois = rs.getInt("mois");
                int nombre = rs.getInt("nombre");
                String moisNom = LocalDate.of(0, mois, 1).getMonth().name();
                statistiques.put(moisNom, nombre);
            }
        }

        return statistiques;
    }
}
