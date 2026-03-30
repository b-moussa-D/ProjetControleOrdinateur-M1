import java.io.*;
import java.net.*;

/**
 * Couche réseau du client de contrôle à distance.
 */
public class Client {

    private String  hote;
    private int     port;
    private Socket  socket;
    private PrintWriter     sortie;
    private BufferedReader  entree;

    public Client(String hote, int port) {
        this.hote = hote;
        this.port = port;
    }

    public boolean connecter() {
        try {
            socket  = new Socket(hote, port);
            sortie  = new PrintWriter(socket.getOutputStream(), true);
            entree  = new BufferedReader(
                          new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean estConnecte() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public String envoyerCommande(String commande) {
        if (!estConnecte()) return "[Erreur : non connecté]";
        try {
            sortie.println(commande);           // envoie la commande
            StringBuilder sb = new StringBuilder();
            String ligne;
            while ((ligne = entree.readLine()) != null) {
                if (ligne.equals("FIN")) break; // marqueur de fin de réponse
                sb.append(ligne).append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            return "[Erreur de communication : " + e.getMessage() + "]";
        }
    }

    public void deconnecter() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        socket = null;
    }
}