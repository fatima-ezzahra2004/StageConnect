

package com.example.projet.services;

import com.example.projet.models.Entreprise;
import com.example.projet.models.Offre;
import com.example.projet.utils.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffreService {
    public List<Offre> getAllOffres() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT m.*, e.nom, e.secteur_activite FROM mission m JOIN entreprise e ON m.entreprise_id = e.idEntreprise");

            while (rs.next()) {
                Entreprise entreprise = new Entreprise(
                        rs.getInt("entreprise_id"),
                        rs.getString("nom"),
                        rs.getString("secteur_activite"),
                        null, null, null
                );

                Offre offre = new Offre(
                        rs.getInt("idMission"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("domaine"),    // domaine avant type
                        rs.getString("type"),
                        rs.getString("niveau_requis"),
                        rs.getInt("duree"),
                        rs.getDate("date_publication").toLocalDate(),
                        rs.getBoolean("est_remunere"),
                        entreprise
                );
                offres.add(offre);
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBConnection.closeConnection(conn);
        }
        return offres;
    }

    public List<String> getAllDomaines() throws SQLException {
        List<String> domaines = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT DISTINCT domaine FROM mission WHERE domaine IS NOT NULL");

            while (rs.next()) {
                domaines.add(rs.getString("domaine"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBConnection.closeConnection(conn);
        }
        return domaines;
    }

    public List<String> getAllTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT DISTINCT type FROM mission WHERE type IS NOT NULL");

            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) DBConnection.closeConnection(conn);
        }
        return types;
    }

    public List<Offre> getOffresFiltrees(String domaine, String type) throws SQLException {
        List<Offre> offres = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            StringBuilder query = new StringBuilder(
                    "SELECT m.*, e.nom, e.secteur_activite FROM mission m " +
                            "JOIN entreprise e ON m.entreprise_id = e.idEntreprise WHERE 1=1"
            );

            List<Object> params = new ArrayList<>();

            if (domaine != null && !domaine.isEmpty() && !domaine.equals("Tous les domaines")) {
                query.append(" AND m.domaine = ?");
                params.add(domaine);
            }
            if (type != null && !type.isEmpty() && !type.equals("Tous les types")) {
                query.append(" AND m.type = ?");
                params.add(type);
            }

            pstmt = conn.prepareStatement(query.toString());

            // Remplissage des paramètres
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Entreprise entreprise = new Entreprise(
                        rs.getInt("entreprise_id"),
                        rs.getString("nom"),
                        rs.getString("secteur_activite"),
                        null, null, null
                );

                Offre offre = new Offre(
                        rs.getInt("idMission"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("domaine"),    // domaine avant type
                        rs.getString("type"),
                        rs.getString("niveau_requis"),
                        rs.getInt("duree"),
                        rs.getDate("date_publication").toLocalDate(),
                        rs.getBoolean("est_remunere"),
                        entreprise
                );

                offres.add(offre);
            }
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) DBConnection.closeConnection(conn);
        }
        return offres;
    }
    public Map<String, Integer> getStatistiquesOffres(int entrepriseId) throws SQLException {
        String query = "SELECT MONTH(date_publication) AS mois, COUNT(*) AS nombre " +
                "FROM mission WHERE entreprise_id = ? " +
                "GROUP BY mois ORDER BY mois";

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