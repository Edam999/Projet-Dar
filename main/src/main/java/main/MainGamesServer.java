package main;

import webserver.WebServer;
import webserver.WebSocketBridge;
import udp.UdpServer;
import MiniGames.CorbaServer;

import java.io.IOException;
import java.net.SocketException;

/**
 * Serveur principal qui démarre tous les composants:
 * - Serveur Web HTTP (port 8080)
 * - Serveur WebSocket (port 8081)
 * - Serveur CORBA (port 1050)
 * - Serveur UDP Broadcast (port 9876)
 */
class MainGameServer {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          GameHub - Plateforme Multijoueur              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // 1. Démarrer le serveur CORBA (dans un thread séparé)
        Thread corbaThread = new Thread(() -> {
            try {
                System.out.println("[1/4] Démarrage du serveur CORBA...");
                CorbaServer.main(new String[]{});
            } catch (Exception e) {
                System.err.println("❌ Erreur CORBA: " + e.getMessage());
            }
        });
        corbaThread.setDaemon(true);
        corbaThread.start();

        // Attendre que CORBA soit prêt
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 2. Démarrer le serveur HTTP
        try {
            System.out.println("[2/4] Démarrage du serveur HTTP...");
            new WebServer();
        } catch (IOException e) {
            System.err.println("❌ Erreur HTTP Server: " + e.getMessage());
            return;
        }

        // 3. Démarrer le serveur WebSocket
        Thread wsThread = new Thread(() -> {
            try {
                System.out.println("[3/4] Démarrage du serveur WebSocket...");
                new WebSocketBridge().onStart();
            } catch (Exception e) {
                System.err.println("❌ Erreur WebSocket: " + e.getMessage());
            }
        });
        wsThread.setDaemon(true);
        wsThread.start();

        // 4. Démarrer le serveur UDP
        Thread udpThread = new Thread(() -> {
            try {
                System.out.println("[4/4] Démarrage du serveur UDP...");
                UdpServer udpServer = new UdpServer();
                udpServer.start();
            } catch (SocketException e) {
                System.err.println("❌ Erreur UDP Server: " + e.getMessage());
            }
        });
        udpThread.setDaemon(true);
        udpThread.start();

        // Attendre que tous les serveurs démarrent
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              ✓ Tous les serveurs sont actifs            ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  HTTP Server      : http://localhost:8080               ║");
        System.out.println("║  WebSocket        : ws://localhost:8081                 ║");
        System.out.println("║  CORBA Naming     : localhost:1050                      ║");
        System.out.println("║  UDP Broadcast    : 0.0.0.0:9876                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("👉 Ouvrez votre navigateur: http://localhost:8080");
        System.out.println();

        // Garder le programme en vie
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\n⚠ Arrêt du serveur...");
        }
    }
}