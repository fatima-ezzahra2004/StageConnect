package com.example.projet.services;

import com.example.projet.models.Entreprise;
import com.example.projet.utils.DBConnection;

import java.sql.*;

public class EntrepriseService {
    public Entreprise getEntrepriseById(int id) {
        String sql = "SELECT * FROM entreprise WHERE idEntreprise = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Entreprise(
                        rs.getInt("idEntreprise"),
                        rs.getString("nom"),
                        rs.getString("secteur_activite"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur récupération entreprise : " + e.getMessage());
        }
        return null;
    }
    public boolean updateEntreprise(Entreprise e) {
        String updateSql = "UPDATE entreprise SET nom = ?, secteur_activite = ?, mot_de_passe = ?, description = ? WHERE idEntreprise = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, e.getNom());
            stmt.setString(2, e.getSecteurActivite());
            stmt.setString(3, e.getMotDePasse());
            stmt.setString(4, e.getDescription());
            stmt.setInt(5, e.getIdEntreprise());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException ex) {
            System.err.println("Erreur mise à jour entreprise : " + ex.getMessage());
            return false;
        }
    }


}
