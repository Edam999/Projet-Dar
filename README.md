📘README – Plateforme de Jeu Multijoueur Tic-Tac-Toe (X/O)

Présentation du Projet

Ce projet est une plateforme multijoueur distribuée permettant à deux joueurs de s’affronter au jeu Tic-Tac-Toe (X/O) à travers plusieurs types de clients et de protocoles réseau.

Il a été réalisé en binôme par Ines Triki & Edam Weli.
L’objectif principal est de mettre en œuvre une plateforme complète basée sur une architecture distribuée modulaire, combinant différents serveurs, technologies et modes de communication.

🏗️ Architecture Globale

L’architecture du système est répartie en 5 modules principaux, chacun indépendant :

Client Desktop (Java TCP)
|
v
Game Server TCP  <-->  WebSocket Server (navigateur)
|
v
CORBA Server (enregistrement + découverte)
|
v
UDP Broadcaster (découverte automatique)


Chaque module joue un rôle spécifique et communique avec les autres via des protocoles dédiés.

🔌 1. Client Desktop (Java)

Le client Desktop est une application Java permettant :

Connexion au serveur de jeu

Détection automatique du serveur grâce au broadcast UDP

Récupération d’informations via CORBA

Jeu en temps réel via TCP

Affichage graphique du plateau X/O

Fonctionnalités

Interface simple et intuitive

Sockets TCP pour les coups envoyés

Gestion de l’état du jeu en direct

Communication avec CORBA pour les joueurs

🎮 2. Serveur de Jeu (TCP)

Le serveur TCP implémente la logique du jeu X/O :

Gestion des sessions entre deux joueurs

Thread séparé pour chaque partie

Vérification des coups

Gestion de l’état du plateau

Détection automatique :

Victoire

Match nul

Coup invalide

Il centralise le gameplay entre les clients desktop ou web.

🌐 3. Serveur Web + WebSocket

Pour rendre le jeu accessible depuis un navigateur web, un serveur WebSocket joue le rôle de pont :

Client Web  <-->  WebSocket Bridge  <-->  Serveur TCP

Fonctions

Traduction WebSocket ↔ TCP

Partage de l’état en temps réel

Communication bidirectionnelle

Jouabilité depuis un simple navigateur

🧩 4. Serveur CORBA

CORBA est utilisé pour :

Enregistrer les joueurs

Leur fournir les informations du serveur de jeu

Eventuellement gérer la création de parties (selon implémentation)

CORBA sert de registre distribué, assurant l’indépendance entre les composants.

📡 5. Serveur UDP Broadcasting

Ce serveur envoie en diffusion (broadcast) un message informant tous les clients qu’un serveur est disponible.

Pourquoi UDP ?

Ne nécessite pas de connexion

Rapide et léger

Idéal pour détecter automatiquement les services du réseau

Lancement du Projet
Pré-requis

Java 8

Serveur CORBA installé

Ports disponibles :

TCP : 12345

WebSocket : 8080

Broadcasting : 8888

▶️ Étapes de démarrage
Compiler les fichiers IDL → lancer l’ORB → lancer CorbaGameServer.

1. Démarrer CORBA

2. Lancer MainGameServer

Il démarre automatiquement :

serveur TCP

serveur CORBA

serveur UDP

WebSocket Bridge

3. Lancer le Client Desktop

Le client détecte automatiquement le serveur via UDP.

4. Jouer depuis le navigateur

Ouvrir :

http://localhost:8080/index.html

🗂️ Arborescence du Projet
C:.
│   .gitignore
│   build.gradle
│   gradlew
│   gradlew.bat
│   README.md
│   settings.gradle
│   
├───.gradle
│   │   file-system.probe
│   │   
│   ├───8.14
│   │   │   gc.properties
│   │   │   
│   │   ├───checksums
│   │   │       checksums.lock
│   │   │       md5-checksums.bin
│   │   │       sha1-checksums.bin
│   │   │       
│   │   ├───executionHistory
│   │   │       executionHistory.bin
│   │   │       executionHistory.lock
│   │   │       
│   │   ├───expanded
│   │   ├───fileChanges
│   │   │       last-build.bin
│   │   │       
│   │   ├───fileHashes
│   │   │       fileHashes.bin
│   │   │       fileHashes.lock
│   │   │       resourceHashesCache.bin
│   │   │       
│   │   └───vcsMetadata
│   ├───buildOutputCleanup
│   │       buildOutputCleanup.lock
│   │       cache.properties
│   │       outputFiles.bin
│   │       
│   └───vcs-1
│           gc.properties
│           
├───.idea
│   │   .gitignore
│   │   compiler.xml
│   │   gradle.xml
│   │   jarRepositories.xml
│   │   misc.xml
│   │   modules.xml
│   │   vcs.xml
│   │   workspace.xml
│   │   
│   └───modules
│           ProjetDar.main.iml
│           
├───build
│   ├───classes
│   │   └───java
│   │       └───main
│   │           └───org
│   │               └───example
│   │                       Main.class
│   │                       
│   ├───generated
│   │   └───sources
│   │       ├───annotationProcessor
│   │       │   └───java
│   │       │       └───main
│   │       └───headers
│   │           └───java
│   │               └───main
│   ├───reports
│   │   └───problems
│   │           problems-report.html
│   │           
│   └───tmp
│       └───compileJava
│               previous-compilation-data.bin
│               
├───client
│   │   build.gradle
│   │   
│   ├───build
│   │   ├───classes
│   │   │   └───java
│   │   │       └───main
│   │   ├───generated
│   │   │   └───sources
│   │   │       ├───annotationProcessor
│   │   │       │   └───java
│   │   │       │       └───main
│   │   │       └───headers
│   │   │           └───java
│   │   │               └───main
│   │   └───tmp
│   │       └───compileJava
│   └───src
│       ├───main
│       │   ├───java
│       │   │   ├───gameserver
│       │   │   │       GameClient.java
│       │   │   │       
│       │   │   ├───gui
│       │   │   │       GameClientTCP.java
│       │   │   │       TicTacToeGUI.java
│       │   │   │       
│       │   │   ├───MiniGames
│       │   │   │       CorbaClient.java
│       │   │   │       UdpClient.java
│       │   │   │       
│       │   │   └───org
│       │   │       └───example
│       │   │               Main.java
│       │   │               
│       │   └───resources
│       └───test
│           ├───java
│           └───resources
├───corba-server
│   │   build.gradle
│   │   
│   ├───build
│   │   ├───classes
│   │   │   └───java
│   │   │       └───main
│   │   │           ├───MiniGames
│   │   │           │       CorbaServer.class
│   │   │           │       PlayerManager.class
│   │   │           │       PlayerManagerHelper.class
│   │   │           │       PlayerManagerHolder.class
│   │   │           │       PlayerManagerImpl.class
│   │   │           │       PlayerManagerOperations.class
│   │   │           │       PlayerManagerPOA.class
│   │   │           │       PlayerService$PlayerSession.class
│   │   │           │       PlayerService.class
│   │   │           │       _PlayerManagerStub.class
│   │   │           │       
│   │   │           └───org
│   │   │               └───example
│   │   │                       Main.class
│   │   │                       
│   │   ├───generated
│   │   │   └───sources
│   │   │       ├───annotationProcessor
│   │   │       │   └───java
│   │   │       │       └───main
│   │   │       └───headers
│   │   │           └───java
│   │   │               └───main
│   │   ├───libs
│   │   │       corba-server-1.0-SNAPSHOT.jar
│   │   │       
│   │   ├───resources
│   │   │   └───main
│   │   │       ├───idl
│   │   │       │       player.idl
│   │   │       │       PlayerManager.idl
│   │   │       │       
│   │   │       └───java
│   │   │           └───MiniGames
│   │   │                   PlayerManager.java
│   │   │                   PlayerManagerHelper.java
│   │   │                   PlayerManagerHolder.java
│   │   │                   PlayerManagerOperations.java
│   │   │                   PlayerManagerPOA.java
│   │   │                   _PlayerManagerStub.java
│   │   │                   
│   │   └───tmp
│   │       ├───compileJava
│   │       │   │   previous-compilation-data.bin
│   │       │   │   
│   │       │   └───compileTransaction
│   │       │       ├───backup-dir
│   │       │       └───stash-dir
│   │       │               CorbaServer.class.uniqueId2
│   │       │               PlayerManager.class.uniqueId4
│   │       │               PlayerManagerHelper.class.uniqueId3
│   │       │               PlayerManagerHolder.class.uniqueId6
│   │       │               PlayerManagerImpl.class.uniqueId0
│   │       │               PlayerManagerOperations.class.uniqueId5
│   │       │               PlayerManagerPOA.class.uniqueId1
│   │       │               _PlayerManagerStub.class.uniqueId7
│   │       │               
│   │       └───jar
│   │               MANIFEST.MF
│   │               
│   └───src
│       ├───main
│       │   ├───java
│       │   │   ├───MiniGames
│       │   │   │       CorbaServer.java
│       │   │   │       PlayerManager.java
│       │   │   │       PlayerManagerHelper.java
│       │   │   │       PlayerManagerHolder.java
│       │   │   │       PlayerManagerImpl.java
│       │   │   │       PlayerManagerOperations.java
│       │   │   │       PlayerManagerPOA.java
│       │   │   │       PlayerService.java
│       │   │   │       _PlayerManagerStub.java
│       │   │   │       
│       │   │   └───org
│       │   │       └───example
│       │   │               Main.java
│       │   │               
│       │   └───resources
│       │       ├───idl
│       │       │       player.idl
│       │       │       PlayerManager.idl
│       │       │       
│       │       └───java
│       │           └───MiniGames
│       │                   PlayerManager.java
│       │                   PlayerManagerHelper.java
│       │                   PlayerManagerHolder.java
│       │                   PlayerManagerOperations.java
│       │                   PlayerManagerPOA.java
│       │                   _PlayerManagerStub.java
│       │                   
│       └───test
│           ├───java
│           └───resources
├───game-server
│   │   build.gradle
│   │   
│   ├───build
│   │   ├───classes
│   │   │   └───java
│   │   │       └───main
│   │   │           ├───gameserver
│   │   │           │       GameServer$ClientHandler.class
│   │   │           │       GameServer$ConnectFourGame.class
│   │   │           │       GameServer$GameSession.class
│   │   │           │       GameServer$Player.class
│   │   │           │       GameServer$TicTacToeGame.class
│   │   │           │       GameServer.class
│   │   │           │       
│   │   │           └───org
│   │   │               └───example
│   │   │                       Main.class
│   │   │                       
│   │   ├───generated
│   │   │   └───sources
│   │   │       ├───annotationProcessor
│   │   │       │   └───java
│   │   │       │       └───main
│   │   │       └───headers
│   │   │           └───java
│   │   │               └───main
│   │   └───tmp
│   │       └───compileJava
│   │               previous-compilation-data.bin
│   │               
│   └───src
│       ├───main
│       │   ├───java
│       │   │   ├───gameserver
│       │   │   │       GameServer.java
│       │   │   │       
│       │   │   └───org
│       │   │       └───example
│       │   │               Main.java
│       │   │               
│       │   └───resources
│       └───test
│           ├───java
│           └───resources
├───gradle
│   └───wrapper
│           gradle-wrapper.jar
│           gradle-wrapper.properties
│           
├───main
│   │   build.gradle
│   │   
│   ├───build
│   │   ├───classes
│   │   │   └───java
│   │   │       └───main
│   │   │           ├───com
│   │   │           │   └───gamehub
│   │   │           │           Main.class
│   │   │           │           
│   │   │           └───main
│   │   │                   MainGameServer.class
│   │   │                   
│   │   ├───generated
│   │   │   └───sources
│   │   │       ├───annotationProcessor
│   │   │       │   └───java
│   │   │       │       └───main
│   │   │       └───headers
│   │   │           └───java
│   │   │               └───main
│   │   └───tmp
│   │       └───compileJava
│   │               previous-compilation-data.bin
│   │               
│   └───src
│       ├───main
│       │   ├───java
│       │   │   ├───com
│       │   │   │   └───gamehub
│       │   │   │           Main.java
│       │   │   │           
│       │   │   └───main
│       │   │           MainGamesServer.java
│       │   │           
│       │   └───resources
│       └───test
│           ├───java
│           └───resources
├───orb.db
│   │   counter
│   │   NC0
│   │   servers.db
│   │   
│   └───logs
├───src
│   ├───main
│   │   ├───java
│   │   │   └───org
│   │   │       └───example
│   │   │               Main.java
│   │   │               
│   │   └───resources
│   └───test
│       ├───java
│       └───resources
├───udp-broadcaster
│   │   build.gradle
│   │   
│   ├───build
│   │   ├───classes
│   │   │   └───java
│   │   │       └───main
│   │   │           ├───org
│   │   │           │   └───example
│   │   │           │           Main.class
│   │   │           │           
│   │   │           └───udp
│   │   │                   UdpServer.class
│   │   │                   
│   │   ├───generated
│   │   │   └───sources
│   │   │       ├───annotationProcessor
│   │   │       │   └───java
│   │   │       │       └───main
│   │   │       └───headers
│   │   │           └───java
│   │   │               └───main
│   │   ├───libs
│   │   │       udp-broadcaster-1.0-SNAPSHOT.jar
│   │   │       
│   │   └───tmp
│   │       ├───compileJava
│   │       │       previous-compilation-data.bin
│   │       │       
│   │       └───jar
│   │               MANIFEST.MF
│   │               
│   └───src
│       ├───main
│       │   ├───java
│       │   │   ├───org
│       │   │   │   └───example
│       │   │   │           Main.java
│       │   │   │           
│       │   │   └───udp
│       │   │           UdpServer.java
│       │   │           
│       │   └───resources
│       └───test
│           ├───java
│           └───resources
└───webserver
│   build.gradle
│   
├───build
│   ├───classes
│   │   └───java
│   │       └───main
│   │           ├───org
│   │           │   └───example
│   │           │           Main.class
│   │           │           
│   │           └───webserver
│   │                   MiniGameWebSocket.class
│   │                   WebServer.class
│   │                   WebSocketBridge$GameRoom.class
│   │                   WebSocketBridge$PlayerSession.class
│   │                   WebSocketBridge.class
│   │                   WebSocketServer.class
│   │                   
│   ├───generated
│   │   └───sources
│   │       ├───annotationProcessor
│   │       │   └───java
│   │       │       └───main
│   │       └───headers
│   │           └───java
│   │               └───main
│   ├───libs
│   │       webserver-1.0-SNAPSHOT.jar
│   │       
│   ├───resources
│   │   └───main
│   │       └───static
│   │               index.html
│   │               
│   └───tmp
│       ├───compileJava
│       │   │   previous-compilation-data.bin
│       │   │   
│       │   └───compileTransaction
│       │       ├───backup-dir
│       │       └───stash-dir
│       │               MiniGameWebSocket.class.uniqueId2
│       │               WebSocketBridge$GameRoom.class.uniqueId0
│       │               WebSocketBridge$PlayerSession.class.uniqueId4
│       │               WebSocketBridge.class.uniqueId1
│       │               WebSocketServer.class.uniqueId3
│       │               
│       └───jar
│               MANIFEST.MF
│               
└───src
├───main
│   ├───java
│   │   ├───org
│   │   │   └───example
│   │   │           Main.java
│   │   │           
│   │   └───webserver
│   │           MiniGameWebSocket.java
│   │           WebServer.java
│   │           WebSocketBridge.java
│   │           WebSocketServer.java
│   │           
│   └───resources
│       └───static
│               index.html
│               
└───test
├───java
└───resources

🧠 Choix Technologiques & Justifications
Technologie	Rôle	Justification
Java	Client, Serveur	Portable, riche en API réseau
TCP	Jeu X/O	Fiable, ordonné, idéal pour tour par tour
UDP	Découverte	Léger, rapide, parfait pour broadcast
CORBA	Registre distribué	Support objets distants + exigence du sujet
WebSockets	Jeu via navigateur	Temps réel, standard web moderne
📌 Fonctionnement Général

Le client reçoit une annonce via UDP.

Il contacte CORBA pour récupérer les informations du serveur.

Il se connecte via TCP au serveur de jeu.

Le serveur crée une session si deux joueurs sont présents.

Les coups sont transmis en temps réel via TCP ou WebSocket.

Le serveur décide du gagnant et termine la partie.

🧪 Tests Réalisés

Partie locale entre deux clients desktop

Partie navigateur ↔ client Java

Déconnexion d’un joueur

Mauvais coups (cases déjà prises, hors plateau)

Détection :

victoire

match nul

Multi-threads (plusieurs parties en parallèle)

📜 Auteurs

Projet réalisé par :

Ines Triki
et
Edam Weli

Projet académique.