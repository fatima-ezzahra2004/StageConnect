package com.example.projet.models;

public class Stagiaire {
    private int idStagiaire;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String competences;
    private String cvPdf;
    private String interets;

    // Constructeur par défaut (nécessaire pour JavaFX et DAO)
    public Stagiaire() {
    }

    // Constructeur complet
    public Stagiaire(int idStagiaire, String nom, String prenom, String email,
                     String motDePasse, String competences, String cvPdf, String interets) {
        this.idStagiaire = idStagiaire;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.competences = competences;
        this.cvPdf = cvPdf;
        this.interets = interets;
    }
    public Stagiaire(String nom, String prenom, String email, String motDePasse,
                     String competences, String cvPdf, String interets) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.competences = competences;
        this.cvPdf = cvPdf;
        this.interets = interets;
    }

    // Getters et setters
    public int getIdStagiaire() {
        return idStagiaire;
    }

    public void setIdStagiaire(int idStagiaire) {
        this.idStagiaire = idStagiaire;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getCompetences() {
        return competences;
    }

    public void setCompetences(String competences) {
        this.competences = competences;
    }

    public String getCvPdf() {
        return cvPdf;
    }

    public void setCvPdf(String cvPdf) {
        this.cvPdf = cvPdf;
    }

    public String getInterets() {
        return interets;
    }

    public void setInterets(String interets) {
        this.interets = interets;
    }

    @Override
    public String toString() {
        return prenom + " " + nom + " (" + email + ")";
    }
}