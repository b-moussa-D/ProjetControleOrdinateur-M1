import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
  Interface graphique Swing du client de controle a distance.
  Utilise la classe Client comme couche reseau.
 * @author Koumba Samb
 */
public class ClientGUI extends JFrame {

    private Client    client;
    private JTextField tfHote;
    private JTextField tfPort;
    private JButton   btnConnecter;
    private JButton   btnDeconnecter;
    private JTextField tfCommande;
    private JButton   btnEnvoyer;
    private JTextArea taResultat;
    private DefaultListModel<String> historiqueModel;
    private List<String> historique;
    private int          historiqueIndex;

    public ClientGUI() {
        setTitle("Client de Controle a Distance");
        setSize(820, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        historique      = new ArrayList<>();
        historiqueIndex = -1;
        construireInterface();
    }

    private void construireInterface() {
        setLayout(new BorderLayout(6, 6));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panneau connexion
        JPanel panCnx = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panCnx.setBorder(BorderFactory.createTitledBorder("Connexion"));
        panCnx.add(new JLabel("Hote :"));
        tfHote = new JTextField("127.0.0.1", 12);
        panCnx.add(tfHote);
        panCnx.add(new JLabel("Port :"));
        tfPort = new JTextField("5000", 6);
        panCnx.add(tfPort);
        btnConnecter    = new JButton("Connecter");
        btnDeconnecter  = new JButton("Deconnecter");
        btnDeconnecter.setEnabled(false);
        panCnx.add(btnConnecter);
        panCnx.add(btnDeconnecter);
        add(panCnx, BorderLayout.NORTH);

        // Zone centrale
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(590);

        taResultat = new JTextArea();
        taResultat.setEditable(false);
        taResultat.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollRes = new JScrollPane(taResultat);
        scrollRes.setBorder(BorderFactory.createTitledBorder("Resultats"));
        split.setLeftComponent(scrollRes);

        historiqueModel = new DefaultListModel<>();
        JList<String> histList = new JList<>(historiqueModel);
        JScrollPane scrollHist = new JScrollPane(histList);
        scrollHist.setBorder(BorderFactory.createTitledBorder("Historique"));
        split.setRightComponent(scrollHist);
        add(split, BorderLayout.CENTER);

        // Zone commande
        JPanel panCmd = new JPanel(new BorderLayout(6, 0));
        panCmd.setBorder(BorderFactory.createTitledBorder("Commande"));
        tfCommande = new JTextField();
        tfCommande.setFont(new Font("Monospaced", Font.PLAIN, 13));
        tfCommande.setEnabled(false);
        btnEnvoyer = new JButton("Envoyer");
        btnEnvoyer.setEnabled(false);
        panCmd.add(tfCommande, BorderLayout.CENTER);
        panCmd.add(btnEnvoyer, BorderLayout.EAST);
        add(panCmd, BorderLayout.SOUTH);

        // Listeners
        btnConnecter.addActionListener(e -> connecter());
        btnDeconnecter.addActionListener(e -> deconnecter());
        btnEnvoyer.addActionListener(e -> envoyerCommande());
        tfCommande.addActionListener(e -> envoyerCommande());

        tfCommande.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP && historiqueIndex > 0) {
                    historiqueIndex--;
                    tfCommande.setText(historique.get(historiqueIndex));
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (historiqueIndex < historique.size() - 1) {
                        historiqueIndex++;
                        tfCommande.setText(historique.get(historiqueIndex));
                    } else { tfCommande.setText(""); }
                }
            }
        });

        histList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !histList.isSelectionEmpty())
                tfCommande.setText(histList.getSelectedValue());
        });
    }

    private void connecter() {
        String hote = tfHote.getText().trim();
        int port;
        try { port = Integer.parseInt(tfPort.getText().trim()); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        client = new Client(hote, port);
        if (client.connecter()) {
            afficher("[Connexion etablie avec " + hote + ":" + port + "]\n");
            btnConnecter.setEnabled(false);
            btnDeconnecter.setEnabled(true);
            tfCommande.setEnabled(true);
            btnEnvoyer.setEnabled(true);
            tfCommande.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                "Impossible de se connecter. Verifiez que le serveur est demarre.",
                "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deconnecter() {
        if (client != null) client.deconnecter();
        afficher("[Deconnecte]\n");
        btnConnecter.setEnabled(true);
        btnDeconnecter.setEnabled(false);
        tfCommande.setEnabled(false);
        btnEnvoyer.setEnabled(false);
    }

    private void envoyerCommande() {
        String cmd = tfCommande.getText().trim();
        if (cmd.isEmpty() || client == null || !client.estConnecte()) return;
        afficher("\n> " + cmd + "\n");
        new Thread(() -> {
            String resultat = client.envoyerCommande(cmd);
            SwingUtilities.invokeLater(() -> {
                afficher(resultat + "\n");
                historique.add(cmd);
                historiqueModel.addElement(cmd);
                historiqueIndex = historique.size();
                tfCommande.setText("");
            });
        }).start();
    }

    private void afficher(String texte) {
        taResultat.append(texte);
        taResultat.setCaretPosition(taResultat.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
    }
}