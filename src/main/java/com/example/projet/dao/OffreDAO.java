package com.example.projet.dao;

import com.example.projet.models.Offre;
import com.example.projet.models.Entreprise;
import com.example.projet.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreDAO {

    public List<Offre> getOffresParEntreprise(int idEntreprise) throws SQLException {
        List<Offre> offres = new ArrayList<>();
        String sql = "SELECT * FROM mission WHERE entreprise_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idEntreprise);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Offre offre = mapResultSetToOffre(rs, idEntreprise);
                offres.add(offre);
            }
        }
        return offres;
    }

    public boolean ajouterOffre(Offre offre) throws SQLException {
        String sql = "INSERT INTO mission (titre, description, domaine, type, niveau_requis, duree, date_publication, est_remunere, entreprise_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setOffreParameters(stmt, offre);
            stmt.setInt(9, offre.getEntreprise().getIdEntreprise());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    offre.setIdMission(generatedKeys.getInt(1));
                }
            }
            return true;
        }
    }

    public boolean modifierOffre(Offre offre) throws SQLException {
        String sql = "UPDATE mission SET titre = ?, description = ?, domaine = ?, type = ?, niveau_requis = ?, " +
                "duree = ?, date_publication = ?, est_remunere = ? WHERE idMission = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setOffreParameters(stmt, offre);
            stmt.setInt(9, offre.getIdMission());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean supprimerOffre(int idMission) throws SQLException {
        String sql = "DELETE FROM mission WHERE idMission = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idMission);
            return stmt.executeUpdate() > 0;
        }
    }

    private Offre mapResultSetToOffre(ResultSet rs, int idEntreprise) throws SQLException {
        Entreprise entreprise = new Entreprise();
        entreprise.setIdEntreprise(idEntreprise);

        return new Offre(
                rs.getInt("idMission"),
                rs.getString("titre"),
                rs.getString("description"),
                rs.getString("domaine"),
                rs.getString("type"),
                rs.getString("niveau_requis"),
                rs.getInt("duree"),
                rs.getDate("date_publication").toLocalDate(),
                rs.getBoolean("est_remunere"),
                entreprise
        );
    }

    private void setOffreParameters(PreparedStatement stmt, Offre offre) throws SQLException {
        stmt.setString(1, offre.getTitre());
        stmt.setString(2, offre.getDescription());
        stmt.setString(3, offre.getDomaine());
        stmt.setString(4, offre.getType());
        stmt.setString(5, offre.getNiveauRequis());
        stmt.setInt(6, offre.getDuree());
        stmt.setDate(7, Date.valueOf(offre.getDatePublication()));
        stmt.setBoolean(8, offre.isEstRemunere());
    }
}