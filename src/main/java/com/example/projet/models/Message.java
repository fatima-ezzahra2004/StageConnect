package com.example.projet.models;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private Integer idExpediteur; // Peut être null pour les messages système
    private int idDestinataire;
    private int idEntreprise;
    private boolean lu;

    public Message() {}

    public Message(String contenu, LocalDateTime dateEnvoi, Integer idExpediteur,
                   int idDestinataire, int idEntreprise) {
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
        this.idExpediteur = idExpediteur;
        this.idDestinataire = idDestinataire;
        this.idEntreprise = idEntreprise;
        this.lu = false;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Integer getIdExpediteur() { return idExpediteur; }
    public void setIdExpediteur(Integer idExpediteur) { this.idExpediteur = idExpediteur; }

    public int getIdDestinataire() { return idDestinataire; }
    public void setIdDestinataire(int idDestinataire) { this.idDestinataire = idDestinataire; }

    public int getIdEntreprise() { return idEntreprise; }
    public void setIdEntreprise(int idEntreprise) { this.idEntreprise = idEntreprise; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}