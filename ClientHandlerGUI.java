import java.io.*;
import java.net.Socket;

/**
 * Gestionnaire de client adapte a l'interface graphique du serveur.
 * Notifie le ServerGUI de chaque evenement via log().
 * @author Koumba Samb
 */
public class ClientHandlerGUI implements Runnable {

    private Socket    socket;
    private String    clientIP;
    private ServerGUI gui;

    public ClientHandlerGUI(Socket socket, ServerGUI gui) {
        this.socket    = socket;
        this.gui       = gui;
        this.clientIP  = socket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String commande;
            while ((commande = in.readLine()) != null) {
                gui.log("[" + clientIP + "] >> " + commande);
                String resultat = executerCommande(commande);
                out.println(resultat);
                out.println("--FIN--");
            }
        } catch (IOException e) {
            gui.log("[Erreur] Session " + clientIP + " : " + e.getMessage());
        } finally {
            fermer();
        }
    }

    private String executerCommande(String commande) {
        StringBuilder sortie = new StringBuilder();
        try {
            String[] cmd;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                cmd = new String[]{"cmd.exe", "/c", commande};
            } else {
                cmd = new String[]{"/bin/sh", "-c", commande};
            }
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String l;
            while ((l = r.readLine()) != null) sortie.append(l).append("\n");
            p.waitFor();
        } catch (Exception e) {
            sortie.append("Erreur : ").append(e.getMessage());
        }
        return sortie.length() > 0 ? sortie.toString().trim() : "(aucune sortie)";
    }

    private void fermer() {
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException e) {
            gui.log("[Erreur fermeture] " + e.getMessage());
        }
    }
}