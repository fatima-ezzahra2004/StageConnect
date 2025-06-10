package com.example.projet.utils;

import com.example.projet.models.Entreprise;
import com.example.projet.models.Stagiaire;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static Stagiaire currentStagiaire;
    private static Entreprise currentEntreprise;
    private static final List<SessionListener> listeners = new ArrayList<>();

    public interface SessionListener {
        void onSessionChange();
    }

    public static void addListener(SessionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void removeListener(SessionListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (SessionListener listener : listeners) {
            listener.onSessionChange();
        }
    }

    public static void loginStagiaire(Stagiaire stagiaire) {
        currentStagiaire = stagiaire;
        currentEntreprise = null;
        notifyListeners();
    }

    public static void loginEntreprise(Entreprise entreprise) {
        currentEntreprise = entreprise;
        currentStagiaire = null;
        notifyListeners();
    }

    public static void logout() {
        currentStagiaire = null;
        currentEntreprise = null;
        notifyListeners();
    }

    public static Stagiaire getCurrentStagiaire() {
        return currentStagiaire;
    }

    public static Entreprise getCurrentEntreprise() {
        return currentEntreprise;
    }

    public static boolean isStagiaireLoggedIn() {
        return currentStagiaire != null;
    }

    public static boolean isEntrepriseLoggedIn() {
        return currentEntreprise != null;
    }

    public static boolean isLoggedIn() {
        return isStagiaireLoggedIn() || isEntrepriseLoggedIn();
    }
}