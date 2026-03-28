import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * Serveur de controle a distance.
 * Ouvre un port TCP et delegue chaque connexion cliente
 * a un thread dedie (ClientHandler).
 *
 * @author Baye Moussa Diongue
 */
public class Server {

    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private boolean running;

    public Server() {
        running = false;
    }

    /** Demarre l'ecoute et accepte les connexions en boucle. */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("[Serveur] Demarre sur le port " + PORT);
            System.out.println("[Serveur] En attente de connexions...");

            while (running) {
                Socket clientSocket = serverSocket.accept();
                String ip = clientSocket.getInetAddress().getHostAddress();
                System.out.println("[Serveur] Nouveau client : " + ip);
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("[Serveur] Erreur : " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    /** Arrete proprement le serveur. */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[Serveur] Arrete.");
            }
        } catch (IOException e) {
            System.err.println("[Serveur] Erreur arret : " + e.getMessage());
        }
    }

    /** @return true si le serveur est en cours d'execution */
    public boolean isRunning() { return running; }

    public static void main(String[] args) {
        new Server().start();
    }
}
