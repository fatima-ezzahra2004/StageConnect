package com.example.projet.services;

import com.example.projet.models.Entreprise;
import com.example.projet.models.Stagiaire;
import com.example.projet.utils.DBConnection;

import java.sql.*;

public class AuthService {

    private static final Connection conn;

    static {
        conn = DBConnection.getConnection();
        if (conn == null) {
            System.err.println("❌ Connexion à la base échouée dans AuthService !");
        }
    }

    public Stagiaire loginStagiaire(String email, String motDePasse) {
        if (conn == null) return null;

        Stagiaire stagiaire = null;
        String query = "SELECT * FROM stagiaire WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);  // mot de passe en clair

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                stagiaire = new Stagiaire(
                        rs.getInt("idStagiaire"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("competences"),
                        rs.getString("cv_pdf"),
                        rs.getString("interets")
                );
                System.out.println("Stagiaire trouvé : " + stagiaire.getPrenom() + " " + stagiaire.getNom());
            } else {
                System.out.println("Aucun stagiaire trouvé avec ces informations.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stagiaire;
    }


    public Entreprise loginEntreprise(String email, String motDePasse) {
        if (conn == null) return null;

        Entreprise entreprise = null;
        String query = "SELECT * FROM entreprise WHERE email = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);  // Comparaison directe sans hash

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                entreprise = new Entreprise(
                        rs.getInt("idEntreprise"),
                        rs.getString("nom"),
                        rs.getString("secteur_activite"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("description")
                );
                System.out.println("Entreprise trouvée : " + entreprise.getNom());
            } else {
                System.out.println("Aucune entreprise trouvée avec ces informations.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entreprise;
    }



    public boolean registerStagiaire(Stagiaire stagiaire) {
        if (conn == null) return false;

        String query = "INSERT INTO stagiaire (prenom, nom, email, mot_de_passe, competences, cv_pdf, interets) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, stagiaire.getPrenom());
            stmt.setString(2, stagiaire.getNom());
            stmt.setString(3, stagiaire.getEmail());
            stmt.setString(4, stagiaire.getMotDePasse());
            stmt.setString(5, stagiaire.getCompetences());
            stmt.setString(6, stagiaire.getCvPdf());
            stmt.setString(7, stagiaire.getInterets());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerEntreprise(Entreprise entreprise) {
        if (conn == null) return false;

        String query = "INSERT INTO entreprise (nom, secteur_activite, email, mot_de_passe, description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, entreprise.getNom());
            stmt.setString(2, entreprise.getSecteurActivite());
            stmt.setString(3, entreprise.getEmail());
            stmt.setString(4, entreprise.getMotDePasse());
            stmt.setString(5, entreprise.getDescription());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
