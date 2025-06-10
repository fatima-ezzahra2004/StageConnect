package com.example.projet.dao;

import com.example.projet.models.Entreprise;
import com.example.projet.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntrepriseDAO {

    /**
     * Récupérer toutes les entreprises depuis la base de données
     * @return Liste des entreprises
     */
    public List<Entreprise> getAllEntreprises() {
        List<Entreprise> entreprises = new ArrayList<>();
        String query = "SELECT * FROM entreprise";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Entreprise entreprise = new Entreprise(
                        rs.getInt("idEntreprise"),
                        rs.getString("nom"),
                        rs.getString("secteur_activite"),
                        rs.getString("email"),
                        rs.getString("mot_de_passe"),
                        rs.getString("description")
                );
                entreprises.add(entreprise);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des entreprises : " + e.getMessage());
        }

        return entreprises;
    }
}
