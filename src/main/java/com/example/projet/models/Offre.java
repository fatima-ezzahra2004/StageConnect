package com.example.projet.models;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Objects;

public class Offre {
    private int idMission;
    private String titre;
    private String description;
    private String type;
    private String domaine;
    private String niveauRequis;
    private int duree; // en semaines
    private LocalDate datePublication;
    private boolean estRemunere;
    private Entreprise entreprise;  // objet Entreprise
    private boolean favori = false;

    public Offre() {
    }

    public Offre(int idMission, String titre, String description, String domaine, String type,
                 String niveauRequis, int duree, LocalDate datePublication, boolean estRemunere) {
        this.idMission = idMission;
        this.titre = titre;
        this.description = description;
        this.domaine = domaine;
        this.type = type;
        this.niveauRequis = niveauRequis;
        this.duree = duree;
        this.datePublication = datePublication;
        this.estRemunere = estRemunere;
    }

    public Offre(int idMission, String titre, String description, String domaine, String type,
                 String niveauRequis, int duree, LocalDate datePublication, boolean estRemunere,
                 Entreprise entreprise) {
        this(idMission, titre, description, domaine, type, niveauRequis, duree, datePublication, estRemunere);
        this.entreprise = entreprise;
    }
    public String getDatePublicationFormatee() {
        if (datePublication != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return datePublication.format(formatter);
        }
        return "";
    }

    public String getRemunerationFormatee() {
        return estRemunere ? "Oui" : "Non";
    }
    // Getters et setters

    public int getIdMission() {
        return idMission;
    }

    public void setIdMission(int idMission) {
        this.idMission = idMission;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getNiveauRequis() {
        return niveauRequis;
    }

    public void setNiveauRequis(String niveauRequis) {
        this.niveauRequis = niveauRequis;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public boolean isEstRemunere() {
        return estRemunere;
    }

    public void setEstRemunere(boolean estRemunere) {
        this.estRemunere = estRemunere;
    }

    public Entreprise getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(Entreprise entreprise) {
        this.entreprise = entreprise;
    }

    public boolean isFavori() {
        return favori;
    }

    public void setFavori(boolean favori) {
        this.favori = favori;
    }
    public String getDureeFormatee() {
        if (duree == 1) {
            return duree + " semaine";
        } else {
            return duree + " semaines";
        }
    }

    // toString, equals, hashCode (optionnel mais recommandé)
    @Override
    public String toString() {
        return "Offre{" +
                "idMission=" + idMission +
                ", titre='" + titre + '\'' +
                ", domaine='" + domaine + '\'' +
                ", type='" + type + '\'' +
                ", niveauRequis='" + niveauRequis + '\'' +
                ", duree=" + duree +
                ", datePublication=" + datePublication +
                ", estRemunere=" + estRemunere +
                ", entreprise=" + (entreprise != null ? entreprise.getNom() : "null") +
                ", favori=" + favori +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offre)) return false;
        Offre offre = (Offre) o;
        return idMission == offre.idMission;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMission);
    }
    public Offre(Offre autre) {
        this.idMission = autre.idMission;
        this.titre = autre.titre;
        this.description = autre.description;
        this.domaine = autre.domaine;
        this.type = autre.type;
        this.niveauRequis = autre.niveauRequis;
        this.duree = autre.duree;
        this.datePublication = autre.datePublication;
        this.estRemunere = autre.estRemunere;
        this.entreprise = autre.entreprise;
        this.favori = autre.favori;
    }
}
