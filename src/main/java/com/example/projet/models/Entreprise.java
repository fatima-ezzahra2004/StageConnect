package com.example.projet.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Entreprise {

    private int idEntreprise;
    private String nom;
    private String secteurActivite;
    private String email;
    private String motDePasse;  // Suppression du hashage
    private String description;

    // Constructeur pour l'enregistrement d'une entreprise
    public Entreprise(String nom, String secteurActivite, String email, String motDePasse, String description) {
        this.nom = nom;
        this.secteurActivite = secteurActivite;
        this.email = email;
        this.motDePasse = motDePasse;  // Mot de passe en clair, sans hashage
        this.description = description;
    }

    // Constructeur pour la récupération depuis la base de données
    public Entreprise(int idEntreprise, String nom, String secteurActivite, String email, String motDePasse, String description) {
        this.idEntreprise = idEntreprise;
        this.nom = nom;
        this.secteurActivite = secteurActivite;
        this.email = email;
        this.motDePasse = motDePasse;  // Mot de passe récupéré sans hashage
        this.description = description;
    }

    // Suppression de la méthode hashPassword
    // Pas besoin de cette méthode maintenant

    // Getters et Setters
    public int getIdEntreprise() {
        return idEntreprise;
    }

    public void setIdEntreprise(int idEntreprise) {
        this.idEntreprise = idEntreprise;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSecteurActivite() {
        return secteurActivite;
    }

    public void setSecteurActivite(String secteurActivite) {
        this.secteurActivite = secteurActivite;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Entreprise() {
        // constructeur vide requis pour certaines opérations (ex: DAO)
    }

}


