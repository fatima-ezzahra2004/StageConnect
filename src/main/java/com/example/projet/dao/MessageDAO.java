package com.example.projet.dao;

import com.example.projet.models.Message;
import com.example.projet.utils.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    // Requêtes SQL préparées
    private static final String INSERT_MESSAGE = """
        INSERT INTO message(contenu, date_envoi, id_expediteur, id_destinataire, id_entreprise, lu) 
        VALUES (?, ?, ?, ?, ?, ?)""";

    private static final String GET_CONVERSATION = """
        SELECT * FROM message 
        WHERE (id_expediteur = ? AND id_destinataire = ?) 
           OR (id_expediteur = ? AND id_destinataire = ?)
           OR (id_entreprise = ? AND id_destinataire = ?)
        ORDER BY date_envoi ASC""";

    private static final String MARK_AS_READ = """
        UPDATE message SET lu = TRUE 
        WHERE id_destinataire = ? AND id_entreprise = ? AND lu = FALSE""";

    private static final String GET_UNREAD_COUNT = """
        SELECT COUNT(*) FROM message 
        WHERE id_destinataire = ? AND id_entreprise = ? AND lu = FALSE""";

    private static final String GET_CONTACTS_FOR_ENTREPRISE = """
        SELECT DISTINCT s.idStagiaire, s.nom, s.prenom 
        FROM stagiaire s 
        JOIN message m ON s.idStagiaire = m.id_destinataire 
        WHERE m.id_entreprise = ?""";

    private static final String GET_CONTACTS_FOR_STAGIAIRE = """
        SELECT DISTINCT e.idEntreprise, e.nom, e.secteur_activite 
        FROM entreprise e 
        JOIN message m ON e.idEntreprise = m.id_entreprise 
        WHERE m.id_destinataire = ?""";

    public boolean ajouterMessage(Message message) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_MESSAGE, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, message.getContenu());
            stmt.setTimestamp(2, Timestamp.valueOf(message.getDateEnvoi()));
            stmt.setObject(3, message.getIdExpediteur(), Types.INTEGER);
            stmt.setInt(4, message.getIdDestinataire());
            stmt.setInt(5, message.getIdEntreprise());
            stmt.setBoolean(6, message.isLu());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    message.setId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Message> getConversation(int idStagiaire, int idEntreprise) throws SQLException {
        List<Message> messages = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CONVERSATION)) {

            // Entreprise → Stagiaire
            stmt.setInt(1, idEntreprise);
            stmt.setInt(2, idStagiaire);
            // Stagiaire → Entreprise
            stmt.setInt(3, idStagiaire);
            stmt.setInt(4, idEntreprise);
            // Messages système
            stmt.setInt(5, idEntreprise);
            stmt.setInt(6, idStagiaire);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapMessageFromResultSet(rs));
                }
            }
        }
        return messages;
    }

    // ... (autres méthodes restent inchangées)

    public void marquerMessagesLus(int idStagiaire, int idEntreprise) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(MARK_AS_READ)) {

            stmt.setInt(1, idEntreprise);
            stmt.setInt(2, idStagiaire);
            stmt.executeUpdate();
        }
    }

    /**
     * Compte les messages non lus pour un stagiaire avec une entreprise spécifique
     */
    public int getUnreadCount(int idStagiaire, int idEntreprise) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_UNREAD_COUNT)) {

            stmt.setInt(1, idStagiaire);
            stmt.setInt(2, idEntreprise);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Récupère la liste des stagiaires avec qui l'entreprise a communiqué
     */
    public List<Contact> getContactsForEntreprise(int idEntreprise) throws SQLException {
        List<Contact> contacts = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CONTACTS_FOR_ENTREPRISE)) {

            stmt.setInt(1, idEntreprise);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new Contact(
                            rs.getInt("idStagiaire"),
                            rs.getString("prenom") + " " + rs.getString("nom")
                    ));
                }
            }
        }
        return contacts;
    }

    /**
     * Récupère la liste des entreprises avec qui le stagiaire a communiqué
     */
    public List<Contact> getContactsForStagiaire(int idStagiaire) throws SQLException {
        List<Contact> contacts = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_CONTACTS_FOR_STAGIAIRE)) {

            stmt.setInt(1, idStagiaire);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contacts.add(new Contact(
                            rs.getInt("idEntreprise"),
                            rs.getString("nom") + " (" + rs.getString("secteur_activite") + ")"
                    ));
                }
            }
        }
        return contacts;
    }

    /**
     * Transforme un ResultSet en objet Message
     */
    private Message mapMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message(
                rs.getString("contenu"),
                rs.getTimestamp("date_envoi").toLocalDateTime(),
                rs.getObject("id_expediteur", Integer.class),
                rs.getInt("id_destinataire"),
                rs.getInt("id_entreprise")
        );
        message.setId(rs.getInt("id_message"));
        message.setLu(rs.getBoolean("lu"));
        return message;
    }

    /**
     * Classe interne pour représenter un contact
     */
    public static class Contact {
        private final int id;
        private final String displayName;

        public Contact(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public int getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}