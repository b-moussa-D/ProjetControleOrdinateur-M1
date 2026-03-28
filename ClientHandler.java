import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gere la session d'un client connecte au serveur.
 * Lit les commandes, les execute en local et renvoie les resultats.
 *
 * @author Baye Moussa Diongue
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private String clientIP;

    public ClientHandler(Socket socket) {
        this.socket   = socket;
        this.clientIP = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try (
            BufferedReader in  = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String commande;
            while ((commande = in.readLine()) != null) {
                System.out.println("[" + clientIP + "] >> " + commande);
                String resultat = executerCommande(commande);
                out.println(resultat);
                out.println("--FIN--");
            }
        } catch (IOException e) {
            System.err.println("[Serveur] Connexion perdue : " + clientIP);
        } finally {
            fermerConnexion();
        }
    }

    /**
     * Execute une commande systeme et capture stdout + stderr.
     * @param commande  La commande a executer
     * @return          Resultat sous forme de String
     */
    private String executerCommande(String commande) {
        StringBuilder sortie = new StringBuilder();
        try {
            String[] cmd;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                cmd = new String[]{"cmd.exe", "/c", commande};
            } else {
                cmd = new String[]{"/bin/sh", "-c", commande};
            }
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process processus = pb.start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(processus.getInputStream()));
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                sortie.append(ligne).append("\n");
            }
            processus.waitFor();
        } catch (Exception e) {
            sortie.append("Erreur d'execution : ").append(e.getMessage());
        }
        return sortie.length() > 0 ? sortie.toString().trim() : "(aucune sortie)";
    }

    /** Ferme la connexion socket avec le client. */
    private void fermerConnexion() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("[Serveur] Deconnexion : " + clientIP);
            }
        } catch (IOException e) {
            System.err.println("[Serveur] Erreur fermeture : " + e.getMessage());
        }
    }
}
