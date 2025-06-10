package com.example.projet.services;

import com.example.projet.dao.StagiaireDAO;
import com.example.projet.models.Stagiaire;
import java.sql.SQLException;

public class StagiaireService {
    private final StagiaireDAO stagiaireDAO = new StagiaireDAO();

    public void updateCvPath(Stagiaire stagiaire) throws SQLException {
        if (stagiaire == null || stagiaire.getIdStagiaire() <= 0) {
            throw new IllegalArgumentException("Stagiaire invalide");
        }

        if (stagiaire.getCvPdf() == null || stagiaire.getCvPdf().isEmpty()) {
            throw new IllegalArgumentException("Chemin du CV invalide");
        }

        stagiaireDAO.updateCvPath(stagiaire.getIdStagiaire(), stagiaire.getCvPdf());
    }

    // Vous pourrez ajouter d'autres méthodes de service ici
}