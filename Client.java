import java.io.*; import java.net.*; import java.util.Scanner;

public class Client {
    private String hote;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client(String hote, int port) {
        this.hote=hote; this.port=port;
    }

    public boolean connecter() {
        try {
            socket = new Socket(hote, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("[Client] Connecte a " + hote + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("[Client] Echec connexion : " + e.getMessage());
            return false;
        }
    }

    public String envoyerCommande(String commande) {
        if (socket == null || socket.isClosed()) return "Non connecte.";
        out.println(commande);
        StringBuilder rep = new StringBuilder();
        try {
            String ligne;
            while ((ligne = in.readLine()) != null && !ligne.equals("--FIN--"))
                rep.append(ligne).append("\n");
        } catch (IOException e) { return "Erreur : " + e.getMessage(); }
        return rep.toString().trim();
    }
    public void deconnecter() {
        try { if (socket != null && !socket.isClosed()) socket.close(); }
        catch (IOException e) { System.err.println(e.getMessage()); }
    }
    public boolean estConnecte() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public static void main(String[] args) {
        Client client = new Client("127.0.0.1", 5000);
        if (!client.connecter()) return;
        Scanner sc = new Scanner(System.in);
        System.out.println("Commandes (exit pour quitter) :");
        while (true) {
            System.out.print("> "); String cmd = sc.nextLine().trim();
            if (cmd.equalsIgnoreCase("exit")) break;
            if (!cmd.isEmpty()) System.out.println(client.envoyerCommande(cmd));
        }
        client.deconnecter(); sc.close();
    }
}