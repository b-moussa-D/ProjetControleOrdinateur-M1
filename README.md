# Logiciel de Controle a Distance -- Master 1 GLSI

Projet realise dans le cadre du module **Java Avance** (Dr. Mouhamed DIOP).
Logiciel client-serveur TCP similaire a SSH avec interface graphique Swing.
Le client envoie des commandes systeme a un serveur distant qui les execute et retourne le resultat.

---

## Groupe 6

| Etudiant | Module |
|----------|--------|
| Baye Moussa Diongue | Serveur TCP (Server.java + ClientHandler.java) |
| Papa Amady Diallo | Client reseau (Client.java) |
| Koumba Samb | Interface graphique Swing (ServerGUI, ClientGUI, ClientHandlerGUI) |

---

## Architecture du projet

```
ProjetControleOrdinateur-M1/
|
|-- Server.java              [OK - Baye Moussa Diongue]   ServerSocket port 5000, thread par client
|-- ClientHandler.java       [OK - Baye Moussa Diongue]   ProcessBuilder, stdout+stderr, marqueur --FIN--
|
|-- Client.java              [OK - Papa Amady Diallo]     connexion TCP, envoi commandes, lecture reponse
|
|-- ServerGUI.java           [OK - Koumba Samb]           fenetre Swing serveur (liste clients + journal)
|-- ClientHandlerGUI.java    [OK - Koumba Samb]           handler avec notification GUI via log()
|-- ClientGUI.java           [OK - Koumba Samb]           fenetre Swing client (connexion + historique)
|
\-- README.md
```

---

## Principe de fonctionnement

```
CLIENT                              SERVEUR
  |                                    |
  |--- connexion TCP port 5000 ------->|
  |--- commande (ex: dir) ------------>|  execute via ProcessBuilder
  |<-- resultat (stdout + stderr) -----|
  |<-- marqueur --FIN-- ---------------|
  |--- nouvelle commande... ---------->|
```

Chaque client obtient son propre thread sur le serveur -- connexions simultanees supportees.

---

## Compilation et execution

```bash
# Compiler toutes les classes
javac *.java

# Interface graphique (livrable final)
java ServerGUI    # terminal 1
java ClientGUI    # terminal 2

# Mode console (tests rapides)
java Server       # terminal 1
java Client       # terminal 2
```

---

## Criteres d'evaluation

| Critere | Poids |
|---------|-------|
| Architecture Client-Serveur | 20% |
| Gestion des Threads | 20% |
| Execution des commandes | 20% |
| Interface utilisateur | 20% |
| Qualite du code | 20% |

---

## Lien video de presentation

[https://youtu.be/bOXdmmrh1W4](https://youtu.be/bOXdmmrh1W4)

---

*Groupe 6 -- Deadline : 12 avril 2026 -- envoitp@gmail.com -- Projet_ControleOrdinateur_G6*