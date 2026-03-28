# Controle Ordinateur a Distance - Master 1 GLSI

Logiciel de controle a distance similaire a SSH, developpe en Java.
Architecture client-serveur TCP avec interface graphique Swing.

## Membres du groupe
| Membre | Module | Fichiers |
|--------|--------|---------|
| Baye Moussa Diongue | Serveur | `Server.java`, `ClientHandler.java` |
| Papa Amady Diallo | Client | `Client.java` |
| Koumba Samb | Interface Graphique | `ServerGUI.java`, `ClientGUI.java`, `ClientHandlerGUI.java` |

## Compilation
```bash
javac *.java
```

## Execution
```bash
# Serveur (mode graphique)
java ServerGUI

# Client (mode graphique)
java ClientGUI

# Mode console (tests rapides)
java Server      # terminal 1
java Client      # terminal 2
```

## Architecture
- `Server.java` : ouvre ServerSocket port 5000, lance un thread par client
- `ClientHandler.java` : execute les commandes via ProcessBuilder, envoie stdout+stderr
- `Client.java` : se connecte, envoie commandes, lit jusqu'a --FIN--
- `ServerGUI.java` : interface Swing serveur (liste clients + journal)
- `ClientGUI.java` : interface Swing client (connexion + historique)
- `ClientHandlerGUI.java` : handler avec notification GUI via log()

## Video de demonstration
[Lien YouTube ici]
