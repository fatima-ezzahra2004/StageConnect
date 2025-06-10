package com.example.projet.dao;

import com.example.projet.models.Candidature;
import com.example.projet.models.Entreprise;
import com.example.projet.models.Offre;
import com.example.projet.models.Stagiaire;
import com.example.projet.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CandidatureDAO {

    /**
     * Ajouter une candidature
     */
    public void ajouterCandidature(Candidature candidature) throws SQLException {
        String query = "INSERT INTO candidature (date_candidature, statut, stagiaire_id, mission_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(candidature.getDateCandidature()));
            stmt.setString(2, candidature.getStatut());
            stmt.setInt(3, candidature.getStagiaire().getIdStagiaire());
            stmt.setInt(4, candidature.getOffre().getIdMission());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("L'insertion a échoué, aucune ligne affectée");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    candidature.setIdCandidature(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Récupérer les candidatures par ID de stagiaire
     */
    public List<Candidature> getCandidaturesByStagiaireId(int stagiaireId) throws SQLException {
        List<Candidature> result = new ArrayList<>();
        String query = """
                SELECT c.idCandidature, c.date_candidature, c.statut, 
                       s.idStagiaire, s.nom AS stagiaire_nom, s.prenom AS stagiaire_prenom, s.email, s.cv_pdf,
                       m.idMission, m.titre, m.description, m.domaine, m.type,
                       e.idEntreprise, e.nom AS entreprise_nom, e.secteur_activite 
                FROM candidature c 
                JOIN stagiaire s ON c.stagiaire_id = s.idStagiaire 
                JOIN mission m ON c.mission_id = m.idMission 
                JOIN entreprise e ON m.entreprise_id = e.idEntreprise 
                WHERE c.stagiaire_id = ? 
                ORDER BY c.date_candidature DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, stagiaireId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                // Création du Stagiaire
                Stagiaire stagiaire = new Stagiaire(
                        rs.getInt("idStagiaire"),
                        rs.getString("stagiaire_nom"),
                        rs.getString("stagiaire_prenom"),
                        rs.getString("email"),
                        null,  // motDePasse
                        null,  // competences
                        rs.getString("cv_pdf"),
                        null   // interets
                );

                // Création de l'Entreprise
                Entreprise entreprise = new Entreprise(
                        rs.getInt("idEntreprise"),
                        rs.getString("entreprise_nom"),
                        rs.getString("secteur_activite"),
                        null, null, null
                );

                // Création de l'Offre
                Offre offre = new Offre(
                        rs.getInt("idMission"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("domaine"),
                        rs.getString("type"),
                        null, // niveauRequis
                        0,    // duree
                        null, // datePublication
                        false, // estRemunere
                        entreprise
                );

                // Création de la Candidature
                Candidature candidature = new Candidature(
                        rs.getInt("idCandidature"),
                        rs.getDate("date_candidature").toLocalDate(),
                        rs.getString("statut"),
                        stagiaire,
                        offre
                );

                result.add(candidature);
            }
        }

        return result;
    }

    /**
     * Récupérer les candidatures reçues pour une entreprise
     */
    public List<Candidature> getCandidaturesRecues(int entrepriseId) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();

        String query = """
                SELECT c.idCandidature, c.date_candidature, c.statut,
                       s.idStagiaire, s.nom AS stagiaire_nom, s.prenom AS stagiaire_prenom, s.email, s.cv_pdf,
                       m.idMission, m.titre 
                FROM candidature c 
                INNER JOIN stagiaire s ON c.stagiaire_id = s.idStagiaire 
                INNER JOIN mission m ON c.mission_id = m.idMission 
                WHERE m.entreprise_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, entrepriseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                // Création du Stagiaire
                Stagiaire stagiaire = new Stagiaire(
                        rs.getInt("idStagiaire"),
                        rs.getString("stagiaire_nom"),
                        rs.getString("stagiaire_prenom"),
                        rs.getString("email"),
                        null,  // motDePasse
                        null,  // competences
                        rs.getString("cv_pdf"),
                        null   // interets
                );

                // Création de l'Offre
                Offre offre = new Offre(
                        rs.getInt("idMission"),
                        rs.getString("titre"),
                        null, // description
                        null, // domaine
                        null, // type
                        null, // niveauRequis
                        0,    // duree
                        null, // datePublication
                        false, // estRemunere
                        null  // entreprise
                );

                // Création de la Candidature
                Candidature candidature = new Candidature(
                        rs.getInt("idCandidature"),
                        rs.getDate("date_candidature").toLocalDate(),
                        rs.getString("statut"),
                        stagiaire,
                        offre
                );

                candidatures.add(candidature);
            }
        }

        return candidatures;
    }
    public boolean updateStatut(int idCandidature, String statut) {
        String query = "UPDATE candidature SET statut = ? WHERE idCandidature = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, statut);
            stmt.setInt(2, idCandidature);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
