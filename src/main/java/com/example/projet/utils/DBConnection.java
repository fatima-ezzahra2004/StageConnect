package com.example.projet.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestionstagiaire";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Ne plus garder de connexion statique
    // private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion établie");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("❌ Erreur de connexion: " + e.getMessage());
            throw new RuntimeException("Erreur de connexion à la base", e);
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("🔒 Connexion fermée");
                }
            } catch (SQLException e) {
                System.err.println("❌ Erreur fermeture connexion: " + e.getMessage());
            }
        }
    }
}