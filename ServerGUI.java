import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

/**
 * Interface graphique Swing du serveur de controle a distance.
 * Affiche la liste des clients connectes et un journal d'activite.
 * @author Koumba Samb
 */
public class ServerGUI extends JFrame {

    private static final int PORT = 5000;

    private JTextArea             logArea;
    private DefaultListModel<String> clientModel;
    private JList<String>         clientList;
    private JButton               btnDemarrer;
    private JButton               btnArreter;
    private ServerSocket          serverSocket;
    private boolean               running = false;

    public ServerGUI() {
        setTitle("Serveur de Controle a Distance");
        setSize(720, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        construireInterface();
    }

    private void construireInterface() {
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panneau de controle
        JPanel panControle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnDemarrer = new JButton("Demarrer le serveur");
        btnArreter  = new JButton("Arreter le serveur");
        btnArreter.setEnabled(false);
        panControle.add(btnDemarrer);
        panControle.add(btnArreter);
        add(panControle, BorderLayout.NORTH);

        // Panneau central
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(200);

        clientModel = new DefaultListModel<>();
        clientList  = new JList<>(clientModel);
        JScrollPane scrollClients = new JScrollPane(clientList);
        scrollClients.setBorder(BorderFactory.createTitledBorder("Clients connectes"));
        split.setLeftComponent(scrollClients);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Journal d'activite"));
        split.setRightComponent(scrollLog);
        add(split, BorderLayout.CENTER);

        // Barre de statut
        JLabel lblStatut = new JLabel("  Statut : Serveur arrete");
        lblStatut.setForeground(Color.RED);
        add(lblStatut, BorderLayout.SOUTH);

        // Listeners
        btnDemarrer.addActionListener(e -> {
            demarrerServeur();
            btnDemarrer.setEnabled(false);
            btnArreter.setEnabled(true);
            lblStatut.setText("  Statut : En cours (port " + PORT + ")");
            lblStatut.setForeground(new Color(0, 140, 0));
        });
        btnArreter.addActionListener(e -> {
            arreterServeur();
            btnDemarrer.setEnabled(true);
            btnArreter.setEnabled(false);
            lblStatut.setText("  Statut : Serveur arrete");
            lblStatut.setForeground(Color.RED);
        });
    }

    private void demarrerServeur() {
        running = true;
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                log("[Serveur] Demarre sur le port " + PORT);
                while (running) {
                    Socket clientSocket = serverSocket.accept();
                    String ip = clientSocket.getInetAddress().getHostAddress();
                    SwingUtilities.invokeLater(() -> clientModel.addElement(ip));
                    log("[Connexion] " + ip);
                    new Thread(() -> {
                        new ClientHandlerGUI(clientSocket, this).run();
                        SwingUtilities.invokeLater(() -> clientModel.removeElement(ip));
                        log("[Deconnexion] " + ip);
                    }).start();
                }
            } catch (IOException e) {
                if (running) log("[Erreur] " + e.getMessage());
            }
        }).start();
    }

    private void arreterServeur() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        } catch (IOException e) { log("[Erreur arret] " + e.getMessage()); }
        log("[Serveur] Arrete.");
        clientModel.clear();
    }

    /** Ajoute un message dans le journal (thread-safe). */
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
    }
}