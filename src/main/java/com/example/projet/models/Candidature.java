package com.example.projet.models;

import java.time.LocalDate;

public class Candidature {
    private int idCandidature;
    private LocalDate dateCandidature;
    private String statut;
    private Stagiaire stagiaire;
    private Offre offre;

    public Candidature(int idCandidature, LocalDate dateCandidature, String statut, Stagiaire stagiaire, Offre offre) {
        this.idCandidature = idCandidature;
        this.dateCandidature = dateCandidature;
        this.statut = statut;
        this.stagiaire = stagiaire;
        this.offre = offre;
    }

    public int getIdCandidature() {
        return idCandidature;
    }

    public void setIdCandidature(int idCandidature) {
        this.idCandidature = idCandidature;
    }

    public LocalDate getDateCandidature() {
        return dateCandidature;
    }

    public String getStatut() {
        return statut;
    }
    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Stagiaire getStagiaire() {
        return stagiaire;
    }

    public Offre getOffre() {
        return offre;
    }
    public String getStagiaireNom() {
        return stagiaire != null ? stagiaire.getNom() : "";
    }

    public String getStagiairePrenom() {
        return stagiaire != null ? stagiaire.getPrenom() : "";
    }

    public String getStagiaireEmail() {
        return stagiaire != null ? stagiaire.getEmail() : "";
    }

    public String getStagiaireCv() {
        return stagiaire != null ? stagiaire.getCvPdf() : "";
    }

    public String getMissionTitre() {
        return offre != null ? offre.getTitre() : "";
    }

}
