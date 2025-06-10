package com.example.projet.dao;

import com.example.projet.models.Stagiaire;
import com.example.projet.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StagiaireDAO {

    public void updateCvPath(int stagiaireId, String cvPath) throws SQLException {
        String query = "UPDATE stagiaire SET cv_pdf = ? WHERE idStagiaire = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cvPath);
            stmt.setInt(2, stagiaireId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Aucun stagiaire trouvé avec l'ID: " + stagiaireId);
            }
        }
    }

    public void updateStagiaire(Stagiaire stagiaire) throws SQLException {
        String query = "UPDATE stagiaire SET nom = ?, prenom = ?, email = ?, " +
                "mot_de_passe = ?, competences = ?, interets = ?, cv_pdf = ? " +
                "WHERE idStagiaire = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, stagiaire.getNom());
            stmt.setString(2, stagiaire.getPrenom());
            stmt.setString(3, stagiaire.getEmail());
            stmt.setString(4, stagiaire.getMotDePasse());
            stmt.setString(5, stagiaire.getCompetences());
            stmt.setString(6, stagiaire.getInterets());
            stmt.setString(7, stagiaire.getCvPdf());
            stmt.setInt(8, stagiaire.getIdStagiaire());

            stmt.executeUpdate();
        }
    }

    public List<Stagiaire> getStagiairesForEntreprise(int idEntreprise) throws SQLException {
        List<Stagiaire> stagiaires = new ArrayList<>();
        String query = "SELECT DISTINCT s.idStagiaire, s.nom, s.prenom, s.email, MAX(m.date_envoi) AS derniere_conversation "
                + "FROM stagiaire s "
                + "JOIN candidature c ON s.idStagiaire = c.stagiaire_id "
                + "JOIN mission o ON c.mission_id = o.idMission "
                + "LEFT JOIN message m ON s.idStagiaire = m.id_destinataire AND m.id_entreprise = o.entreprise_id "
                + "WHERE o.entreprise_id = ? "
                + "GROUP BY s.idStagiaire, s.nom, s.prenom, s.email "
                + "ORDER BY derniere_conversation DESC, s.nom ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idEntreprise);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Stagiaire stagiaire = new Stagiaire();
                    stagiaire.setIdStagiaire(rs.getInt("idStagiaire"));
                    stagiaire.setNom(rs.getString("nom"));
                    stagiaire.setPrenom(rs.getString("prenom"));
                    stagiaire.setEmail(rs.getString("email"));
                    stagiaires.add(stagiaire);
                }
            }
        }
        return stagiaires;
    }

    public Stagiaire getStagiaireById(int id) throws SQLException {
        String query = "SELECT * FROM stagiaire WHERE idStagiaire = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Stagiaire stagiaire = new Stagiaire();
                    stagiaire.setIdStagiaire(rs.getInt("idStagiaire"));
                    stagiaire.setNom(rs.getString("nom"));
                    stagiaire.setPrenom(rs.getString("prenom"));
                    stagiaire.setEmail(rs.getString("email"));
                    stagiaire.setMotDePasse(rs.getString("mot_de_passe"));
                    stagiaire.setCompetences(rs.getString("competences"));
                    stagiaire.setInterets(rs.getString("interets"));
                    stagiaire.setCvPdf(rs.getString("cv_pdf"));
                    return stagiaire;
                }
            }
        }
        return null;
    }

    public Stagiaire getStagiaireByEmail(String email) throws SQLException {
        String query = "SELECT * FROM stagiaire WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Stagiaire stagiaire = new Stagiaire();
                    stagiaire.setIdStagiaire(rs.getInt("idStagiaire"));
                    stagiaire.setNom(rs.getString("nom"));
                    stagiaire.setPrenom(rs.getString("prenom"));
                    stagiaire.setEmail(rs.getString("email"));
                    stagiaire.setMotDePasse(rs.getString("mot_de_passe"));
                    stagiaire.setCompetences(rs.getString("competences"));
                    stagiaire.setInterets(rs.getString("interets"));
                    stagiaire.setCvPdf(rs.getString("cv_pdf"));
                    return stagiaire;
                }
            }
        }
        return null;
    }
}