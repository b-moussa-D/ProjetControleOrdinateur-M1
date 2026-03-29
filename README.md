# Logiciel de Controle a Distance — Master 1 GLSI

Projet realise dans le cadre du module **Java Avance** (Dr. Mouhamed DIOP).
Le logiciel permet a un poste client d'envoyer des commandes systeme a un serveur distant via une connexion TCP,
de recuperer les resultats et de les afficher. Le principe est similaire a SSH, mais developpe integralement en Java
avec une interface graphique Swing.

---

## Groupe

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
|-- Server.java               [OK - Baye]   ecoute sur le port 5000, un thread par client
|-- ClientHandler.java        [OK - Baye]   execution des commandes via ProcessBuilder, envoi du resultat
|
|-- Client.java               [A COMPLETER - Papa Amady]   connexion TCP, envoi commandes, lecture reponse
|
|-- ServerGUI.java            [A COMPLETER - Koumba]   interface Swing du serveur (liste clients + journal)
|-- ClientHandlerGUI.java     [A COMPLETER - Koumba]   handler adapte a la GUI, notifie via log()
|-- ClientGUI.java            [A COMPLETER - Koumba]   interface Swing du client (connexion + historique)
|
`-- README.md
```

> Quand un fichier est termine, le membre concerne le commit et le push sur la branche **main** du depot.
> L'integration finale (test multi-clients + corrections eventuelles) se fait ensemble.

---

## Principe de fonctionnement

```
CLIENT                          SERVEUR
  |                                |
  |--- connexion TCP port 5000 --->|
  |--- commande (ex: dir) -------->|  execute via ProcessBuilder
  |<-- resultat (stdout+stderr) ---|
  |<-- marqueur --FIN-- -----------|
  |--- nouvelle commande... ------>|
```

Le serveur gere plusieurs clients en meme temps : chaque connexion entrante lance un thread dedie (`ClientHandler`).
Le marqueur `--FIN--` signale la fin d'une reponse, ce qui permet au client de savoir quand arreter de lire.

---

## Compilation et execution

Depuis le dossier racine (quand tous les fichiers seront presents) :

```bash
# Compiler
javac *.java

# Lancer le serveur (interface graphique)
java ServerGUI

# Lancer le client (interface graphique) — dans un autre terminal
java ClientGUI

# Mode console pour tests rapides
java Server   # terminal 1
java Client   # terminal 2
```

Pour tester avec plusieurs clients, ouvrir plusieurs terminaux et lancer `java ClientGUI` (ou `java Client`) autant de fois que necessaire. Le serveur les gere tous en parallele.

---

## Commandes de test utiles

```bash
# Windows
dir
ipconfig
whoami
echo test de connexion

# Linux / macOS
ls -la
ifconfig
whoami
echo "test"
```

---

## Criteres d'evaluation (rappel)

| Critere | Poids |
|---------|-------|
| Architecture Client-Serveur | 20% |
| Gestion des Threads | 20% |
| Execution des commandes | 20% |
| Interface utilisateur | 20% |
| Qualite du code | 20% |

---

## Lien video de presentation

> A completer apres enregistrement : [YouTube — lien a ajouter]

---

*Deadline : 12 avril 2026 — envoi a envoitp@gmail.com, objet : Projet_ControleOrdinateur_Gx*
