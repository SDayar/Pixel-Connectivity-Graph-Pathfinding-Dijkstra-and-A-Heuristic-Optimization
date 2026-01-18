/**
 * @author SAIFIDINE Dayar
 * @version 1.0
 */

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**  
 * Interface graphique principale pour l'application.
 * Permet de charger une image, selectionner des points de depart et d'arrivee,
 * executer des algorithmes (Dijkstra, A*) et visualiser les resultats.
 * Implemente le double buffering pour ameliorer les performances d'affichage.
 */
public class InterfaceGraphique extends Frame{
    
    
    /**
     * Bouton pour ouvrir le dialogue de selection de fichier.
     */
    private Button choisirButton;
    
    /**
     * Label affichant le nom du fichier image selectionne.
     */
    private Label fichierLabel;
    
    /**
     * Liste deroulante pour choisir l'algorithme (Dijkstra ou A*).
     */
    private Choice algoChoice;
    
    /**
     * Liste deroulante pour choisir l'heuristique (Manhattan, Euclidienne, Octile).
     * N'est activee que lorsque A* est selectionne.
     */
    private Choice heuristiqueChoice;
    
    /**
     * Bouton pour lancer l'execution de l'algorithme selectionne.
     */
    private Button executerButton;
    
    /**
     * Bouton pour reinitialiser la selection et les resultats.
     */
    private Button resetButton;
    
    /**
     * Zone de dessin pour afficher l'image et le chemin calcule.
     * Implemente le double buffering pour eviter le scintillement.
     */
    private Canvas imageCanvas;
    
    /**
     * Zone de texte pour afficher les instructions a l'utilisateur.
     */
    private TextArea infoArea;
    
    /**
     * Zone de texte pour afficher les resultats detailles de l'execution.
     */
    private TextArea resultArea;
    
    /**
     * Label affichant le cout total du chemin trouve.
     */
    private Label coutLabel;
    
    /**
     * Label affichant le nombre de sommets visites pendant l'execution.
     */
    private Label sommetsLabel;
    
    /**
     * Label affichant le temps d'execution de l'algorithme en millisecondes.
     */
    private Label tempsLabel;
    
    
    /**
     * Fichier image selectionne par l'utilisateur.
     */
    private File imageFile;
    
    /**
     * Image originale chargee depuis le fichier.
     */
    private BufferedImage originalImage;
    
    /**
     * Image redimensionnee pour l'affichage dans l'interface.
     */
    private BufferedImage displayedImage;
    
    /**
     * Graphe genere a partir de l'image traitee.
     */
    private Graph graph;
    
    /**
     * Etiquette du sommet de depart selectionne (-1 si aucun).
     */
    private int depart = -1;
    
    /**
     * Etiquette du sommet d'arrivee selectionne (-1 si aucun).
     */
    private int arrivee = -1;
    
    /**
     * Liste des etiquettes des sommets formant le chemin optimal trouve.
     */
    private List<Integer> chemin;
    
    /**
     * Image tampon pour le double buffering, permettant d'eviter le scintillement
     * lors du redessin de la zone graphique.
     */
    private Image buffer;
    private Djikstra djikstra;
    private AEtoile aEtoile;
    private Sommet sommetArrivee;
    private InterfaceHeuristique h;
  


    
    /**
     * Constructeur principal de l'interface graphique.
     * Initialise l'interface utilisateur et configure les ecouteurs d'evenements.
     */
    public InterfaceGraphique() {
        super("Scénario 2 - Graphes et Image ( Djikstra et modélisation )");
        
        initUI();
        configurerEcouteurs();
        setResizable(false);
    }
    
    /**
     * Initialise tous les composants de l'interface utilisateur.
     * Organise les panneaux, configure les layouts et definit les proprietes visuelles.
     */
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setSize(1100, 750);
        
        Panel panelGauche = new Panel();
        panelGauche.setLayout(new GridLayout(6, 1, 10, 10));
        panelGauche.setBackground(new Color(240, 240, 240));
        panelGauche.setPreferredSize(new Dimension(300, 0));

        
        Panel panelFichier = creerPanelCarte("FICHIER IMAGE");//Panel pour choisir un fichier
        choisirButton = new Button("CHOISIR UNE IMAGE");
        choisirButton.setBackground(new Color(0, 150, 200));
        choisirButton.setForeground(Color.WHITE);
        choisirButton.setFont(new Font("Arial", Font.BOLD, 12));
        fichierLabel = new Label("Aucun fichier selectionné", Label.CENTER);
        fichierLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        panelFichier.setLayout(new BorderLayout(5, 5));
        panelFichier.add(choisirButton, BorderLayout.NORTH);
        panelFichier.add(fichierLabel, BorderLayout.CENTER);
         
        Panel panelAlgo = creerPanelCarte("ALGORITHME");//Panel pour chosir l'algorithme à exécuter
        panelAlgo.setLayout(new GridLayout(2, 2, 10, 10));
        panelAlgo.add(new Label("", Label.RIGHT));
        
        algoChoice = new Choice();
        algoChoice.add("Dijkstra");
        algoChoice.add("A*");
        panelAlgo.add(algoChoice);
        panelAlgo.add(new Label("Heuristique:", Label.RIGHT));
        heuristiqueChoice = new Choice();
        heuristiqueChoice.add("Manhattan");
        heuristiqueChoice.add("Euclidienne");
        heuristiqueChoice.add("Octile");
        heuristiqueChoice.setEnabled(false);
        panelAlgo.add(heuristiqueChoice);
        
    
        Panel panelActions = creerPanelCarte("ACTIONS");
        panelActions.setLayout(new GridLayout(2, 1, 10, 10));
        executerButton = new Button("EXECUTER L'ALGORITHME");
        executerButton.setBackground(Color.GREEN);
        executerButton.setForeground(Color.WHITE);
        executerButton.setFont(new Font("Arial", Font.BOLD, 11));
        executerButton.setEnabled(false);
        resetButton = new Button("REINITIALISER");
        resetButton.setBackground(Color.RED);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Arial", Font.BOLD, 11));
        resetButton.setEnabled(false);
        panelActions.add(executerButton);
        panelActions.add(resetButton);
        
       
        Panel panelResultats = creerPanelCarte("Résultats ");
        panelResultats.setLayout(new GridLayout(2, 2, 10, 25));

       
        Panel panelCout = new Panel(new BorderLayout());//Panel du cout
        Label titreCout = new Label("Cout", Label.CENTER);
        titreCout.setFont(new Font("Arial", Font.BOLD, 10));
        panelCout.add(titreCout, BorderLayout.NORTH);

        coutLabel = new Label("0.0", Label.CENTER);
        coutLabel.setFont(new Font("Arial", Font.BOLD, 10)); // Gros
        coutLabel.setForeground(new Color(0, 120, 80));
        panelCout.add(coutLabel, BorderLayout.CENTER);

       
        Panel panelSommets = new Panel(new BorderLayout());//Panel où sera affiché le nombre de sommets vistités
        Label titreSommets = new Label("Sommets visités ", Label.CENTER);
        titreSommets.setFont(new Font("Arial", Font.BOLD, 10));
        panelSommets.add(titreSommets, BorderLayout.NORTH);

        sommetsLabel = new Label("0/0", Label.CENTER);
        sommetsLabel.setFont(new Font("Arial", Font.BOLD, 10)); 
        sommetsLabel.setForeground(new Color(0, 100, 180));
        panelSommets.add(sommetsLabel, BorderLayout.CENTER);
       
        panelResultats.add(panelCout);
        panelResultats.add(panelSommets);
       
        panelGauche.add(panelFichier);
        panelGauche.add(panelAlgo);
        panelGauche.add(panelActions);
        panelGauche.add(panelResultats);
        
        panelGauche.add(new Panel());
        panelGauche.add(new Panel());
        
        
        Panel panelCentre = new Panel(new BorderLayout(10, 10));//Création du paneau central
        
        // Canvas avec double buffering
        imageCanvas = new Canvas() {
            /**
             * Surcharge de la methode paint pour implementer le double buffering.
             * Dessine d'abord sur le buffer puis copie le buffer sur l'ecran.
             * @param g Contexte graphique pour le dessin
             */
            @Override
            public void update(Graphics g) {
                if (buffer == null) {
                    buffer = createImage(getWidth(), getHeight());
                }
                Graphics bufferGraphics = buffer.getGraphics();
                peindre(bufferGraphics);
                g.drawImage(buffer, 0, 0, null);
                bufferGraphics.dispose();
            }
            
            /**
             * Methode de dessin principale appelee par update().
             * Affiche soit un message d'attente, soit l'image avec le chemin.
             * @param g Contexte graphique pour le dessin
             */
            @Override
            public void paint(Graphics g) {
                update(g);
            }
            
            /**
             * Logique de dessin reelle, separee pour la clarte.
             * @param g Contexte graphique pour le dessin qui sera effectuée
             */
            private void peindre(Graphics g) {
                if (displayedImage == null) {
                    // Afficher un message si aucune image n'est message
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.DARK_GRAY);
                    g.setFont(new Font("Arial", Font.BOLD, 18));
                    String msg = "VISUALISATION DU GRAPHE D'IMAGE";
                    FontMetrics fm = g.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(msg)) / 2;
                    int y = getHeight() / 2 - 20;
                    g.drawString(msg, x, y);
                    
                    g.setFont(new Font("Arial", Font.PLAIN, 14));
                    msg = "Choisissez une image pour commencer";
                    fm = g.getFontMetrics();
                    x = (getWidth() - fm.stringWidth(msg)) / 2;
                    y = getHeight() / 2 + 10;
                    g.drawString(msg, x, y);
                } else {
                    // Centrer l'image
                    int x = (getWidth() - displayedImage.getWidth()) / 2;
                    int y = (getHeight() - displayedImage.getHeight()) / 2;
                    g.drawImage(displayedImage, x, y, null);
                    
                    // Dessiner le chemin et les points
                    dessinerCheminEtPoints(g, x, y);
                }
            }
        };
        imageCanvas.setBackground(new Color(45, 45, 50));
        imageCanvas.setPreferredSize(new Dimension(700, 500));
        
        
        Panel panelInstructions = creerPanelCarte("INSTRUCTIONS");//Panel 
        infoArea = new TextArea(6, 50);
        
        infoArea.setText("Veuillez suivre les étapes suivantes : \n\n" +
                        "1. Choisissez une image (JPG, PNG, BMP, GIF) de taille maximale 1000 x 1000 \n" +
                        "2. Cliquez sur l'image pour selectionner le point de depart ( inquiqué D  en rouge)\n" +
                        "3. Cliquez a nouveau pour selectionner le point d'arrivee ( indiqué A en vert )\n" +
                        "4. Choisissez l'algorithme et l'heuristique (pour A*)\n" +
                        "5. Cliquez sur EXECUTER pour trouver le chemin optimal et il est possible de visualiser les résultats dans le panel en dessous.");
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(250, 250, 252));
        infoArea.setForeground(new Color(50, 50, 50));  
        infoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInstructions.setLayout(new BorderLayout());
        panelInstructions.add(infoArea, BorderLayout.CENTER);
        
        panelCentre.add(imageCanvas, BorderLayout.CENTER);
        panelCentre.add(panelInstructions, BorderLayout.SOUTH);
        
        
        Panel panelBas = new Panel(new BorderLayout(5, 5));//Panel du bas où seront affichés les résultats avec les détails
        Panel panelTitreResultats = new Panel();
        panelTitreResultats.setBackground(new Color(220, 230, 240));
        panelTitreResultats.add(new Label("RESULTATS D'EXECUTION", Label.CENTER));
        panelTitreResultats.setFont(new Font("Arial", Font.BOLD, 14));
        
        resultArea = new TextArea(5, 80);
        resultArea.setText("Aucun fichier chargé. Veuillez selectionner une image.");
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(250, 250, 252));
        resultArea.setForeground(new Color(50, 50, 50));
        resultArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        panelBas.add(panelTitreResultats, BorderLayout.NORTH);
        panelBas.add(resultArea, BorderLayout.CENTER);
        add(panelGauche, BorderLayout.WEST);
        add(panelCentre, BorderLayout.CENTER);
        add(panelBas, BorderLayout.SOUTH);
        Dimension ecran = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((ecran.width - 1100) / 2, (ecran.height - 750) / 2);
        setVisible(true);
    }
    
    /**
     * Cree un panneau avec un style de "carte" (fond blanc, bordure implicite, titre).
     * @param titre Titre a afficher en haut du panneau
     * @return Panneau stylise
     */
    private Panel creerPanelCarte(String titre) {
        Panel panel = new Panel();
        panel.setBackground(new Color(240, 242, 245));
        panel.setLayout(new BorderLayout());
        
        if (titre != null && !titre.isEmpty()) {
            Panel panelTitre = new Panel();
            panelTitre.setBackground(new Color(220, 225, 230));
            panelTitre.setLayout(new FlowLayout(FlowLayout.LEFT));
            Label labelTitre = new Label("  " + titre + "  ");
            labelTitre.setFont(new Font("Arial", Font.BOLD, 12));
            labelTitre.setForeground(new Color(0, 80, 160));
            panelTitre.add(labelTitre);
            panel.add(panelTitre, BorderLayout.NORTH);
        }
        
        Panel panelContenu = new Panel();
        panelContenu.setBackground(Color.WHITE);
        panel.add(panelContenu, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Configure tous les ecouteurs d'evenements pour les composants interactifs.
     * Gere les clics de boutons, les selections dans les listes et les clics sur l'image.
     */
    private void configurerEcouteurs() {
       
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        // Boutons
        choisirButton.addActionListener(e -> choisirFichier());
        executerButton.addActionListener(e -> executerAlgorithme());
        resetButton.addActionListener(e -> reinitialiser());
        
        
        algoChoice.addItemListener(e -> {
            boolean estAStar = "A*".equals(algoChoice.getSelectedItem());
            heuristiqueChoice.setEnabled(estAStar);
            if (!estAStar) {
                heuristiqueChoice.select(0);
            }
        });// Gestion de l'activation/desactivation de l'heuristique selon l'algorithme
        
        // Evenement de clics sur l'image pour selectionner depart/arrivee
        imageCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (displayedImage != null && graph != null) {
                    int largeurCanvas = imageCanvas.getWidth();
                    int hauteurCanvas = imageCanvas.getHeight();
                    int largeurImage = displayedImage.getWidth();
                    int hauteurImage = displayedImage.getHeight();
                    
                    int decalageX = (largeurCanvas - largeurImage) / 2;
                    int decalageY = (hauteurCanvas - hauteurImage) / 2;
                    
                    if (e.getX() >= decalageX && e.getX() < decalageX + largeurImage &&
                        e.getY() >= decalageY && e.getY() < decalageY + hauteurImage) {
                        
                        int x = e.getX() - decalageX;
                        int y = e.getY() - decalageY;
                        selectionnerPoint(x, y);
                    }
                }
            }
        });
    }
    
    /**
     * Ouvre un dialogue de selection de fichier pour choisir une image.
     * Gere proprement les ressources avec un bloc try-finally.
     */
    private void choisirFichier() {
        FileDialog dialogue = new FileDialog(this, "Choisir une image", FileDialog.LOAD);
        dialogue.setFile("*.jpg;*.jpeg;*.png;*.gif;*.bmp");
        
        
            dialogue.setVisible(true); 
            
            String fichier = dialogue.getFile();
            String chemin = dialogue.getDirectory();
            if (fichier != null) {//Si  on arrive à charger le fichier
                
                // On va essayer de redimentionner l'image pour l'affichage
                try {
                     imageFile = new File(chemin+fichier);
                    originalImage = javax.imageio.ImageIO.read(imageFile);
                    buffer.flush();
                    buffer = null; // On nettoie le buffer avant de reafficher une nouvelle photo
                    // Redimensionner si trop grande pour l'affichage 
                    int largeurMax = 700, hauteurMax = 500;
                    if (originalImage.getWidth() > largeurMax || originalImage.getHeight() > hauteurMax) {
                        double echelle = Math.min(
                            (double) largeurMax / originalImage.getWidth(),
                            (double) hauteurMax / originalImage.getHeight()
                        );
                        
                        int nouvelleLargeur = (int) (originalImage.getWidth() * echelle);
                        int nouvelleHauteur = (int) (originalImage.getHeight() * echelle);
                        
                        Image imageRedimensionnee = originalImage.getScaledInstance(nouvelleLargeur, nouvelleHauteur, Image.SCALE_SMOOTH);
                        displayedImage = new BufferedImage(nouvelleLargeur, nouvelleHauteur, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = displayedImage.createGraphics();
                        g2d.drawImage(imageRedimensionnee, 0, 0, null);
                        g2d.dispose();
                    } else {
                        displayedImage = new BufferedImage(
                            originalImage.getWidth(),
                            originalImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = displayedImage.createGraphics();
                        g2d.drawImage(originalImage, 0, 0, null);
                        g2d.dispose();
                    }
                    
                    traiterImage();
                    imageCanvas.repaint();//On va afficher la photo redimensionnée.
                    
                }catch (NullPointerException e) {
                    resultArea.setText("Erreur de chargement: " + e.getMessage());
                    infoArea.setText("Erreur de chargement: de l'image."+e.getMessage());
                }catch(IOException e){
                    resultArea.setText("Erreur de chargement: " + e.getMessage());
                    infoArea.setText("Erreur de chargement: de l'image."+e.getMessage());
                }
            }
       
            dialogue.dispose();
        
    }
    
    /**
     * Traite l'image selectionnee pour generer le graphe correspondant.
     * S'execute dans un thread separe pour ne pas bloquer l'interface utilisateur.
     */
    private void traiterImage() {
        if (imageFile == null){
            resultArea.setText("Chargement de l'image échouée. Assurez-vous que votre image se trouve bien dans le dossier ressources.");
            infoArea.setText("Erreur de chargement: de l'image.");
            return;
        }
        new Thread(() -> {
            try {
                TraitementPhoto tp = new TraitementPhoto(imageFile);
                tp.launch();
                graph = tp.getGraph();
                if (graph != null) {
                    EventQueue.invokeLater(() -> {
                        resultArea.setText("Image traitée avec succés.\n" +
                                        "Graphe créé : " + graph.getNombreSommets() + 
                                        " sommets (" + graph.getLigne() + "x" + 
                                        graph.getColonne() + ")\n" +
                                        "Selectionnez depart et arrivee.");
                        
                        infoArea.setText("Selectionnez un point de départ et ensuite un point d'arrivée.");
                        fichierLabel.setText(imageFile.getName());//On met à jour le nom du fichier
                        resetButton.setEnabled(true);
                        //reinitialiser();
                        
                        
                    });
                }
                
            } catch (IllegalArgumentException e) {
                // Une erreur  de chargement (fichier introuvable, format non supporté)
                EventQueue.invokeLater(() -> {
                    resultArea.setText(e.getMessage());
                    infoArea.setText(e.getMessage());
                    fichierLabel.setText("Aucun fichier selectionné");
                    
                    imageCanvas.repaint();
                });
                
            } catch (IllegalStateException e) {
                // Erreur de traitement (image trop grande, graphe non initialise)
                EventQueue.invokeLater(() -> {
                    resultArea.setText("Erreur de traitement: " + e.getMessage());
                    infoArea.setText("Erreur lors du chargement de l'image.");
                });
                
            } catch (RuntimeException e) {
                // Erreur generale lors du traitement
                EventQueue.invokeLater(() -> {
                    resultArea.setText("Erreur technique: " + e.getMessage());
                    infoArea.setText("Erreur lors du chargement de l'image.");
                });
                
            } catch (Exception e) {
                // Erreur inattendue
                EventQueue.invokeLater(() -> {
                    resultArea.setText("Erreur inattendue: " + e.getMessage());
                    infoArea.setText("Erreur lors du chargement de l'image.");
                });
            }
        }).start();
}
    
    /**
     * Gere la selection d'un point sur l'image (depart ou arrivee).
     * Convertit les coordonnees du clic en etiquette de sommet dans le graphe.
     * @param x Coordonnee X relative a l'image affichee
     * @param y Coordonnee Y relative a l'image affichee
     */
    private void selectionnerPoint(int x, int y) {
        if (graph == null) return;
        
        // Convertir les coordonnees de l'image affichee vers l'image originale
        double echelleX = (double) originalImage.getWidth() / displayedImage.getWidth();
        double echelleY = (double) originalImage.getHeight() / displayedImage.getHeight();
        
        int xOriginal = (int) (x * echelleX);
        int yOriginal = (int) (y * echelleY);
        
        // Calculer l'etiquette du sommet (ligne * largeur + colonne)
        int etiquette = yOriginal * originalImage.getWidth() + xOriginal;
        
        if (etiquette < 0 || etiquette >= graph.getNombreSommets()) {
            resultArea.setText("Point hors de l'image !");
            return;
        }
        
        if (depart == -1) {
            depart = etiquette;
            infoArea.setText("Depart selectionne a (" + yOriginal + "," + xOriginal + ")\n" +
                           "Cliquez pour selectionner le point d'arrivee (vert).");
            executerButton.setEnabled(false);
            resultArea.setText("Depart: (" + yOriginal + "," + xOriginal + ")\n" +
                             "Attente de la selection de l'arrivee...");
        } else if (arrivee == -1) {
            arrivee = etiquette;
            infoArea.setText("Pret a executer.\n" +
                           "Depart: (" + graph.getSommet(depart).getCoordonnee().getLigne() + 
                           "," + graph.getSommet(depart).getCoordonnee().getColonne() + ")\n" +
                           "Arrivee: (" + yOriginal + "," + xOriginal + ")");
            executerButton.setEnabled(true);
            resultArea.setText("Depart: (" + graph.getSommet(depart).getCoordonnee().getLigne() + 
                             "," + graph.getSommet(depart).getCoordonnee().getColonne() + ")\n" +
                             "Arrivee: (" + yOriginal + "," + xOriginal + ")\n" +
                             "Pret pour l'execution de l'algorithme.");
        } else {
            // Si on clique a nouveau, on recommence
            depart = etiquette;
            arrivee = -1;
            infoArea.setText("Depart selectionne a (" + yOriginal + "," + xOriginal + ")\n" +
                           "Cliquez pour selectionner le point d'arrivee (vert).");
            executerButton.setEnabled(false);
            resultArea.setText("Depart: (" + yOriginal + "," + xOriginal + ")\n" +
                             "Attente de la selection de l'arrivee...");
        }
        
        imageCanvas.repaint();
    }
    
    /**
     * Dessine le chemin trouve et les points de depart/arrivee sur l'image.
     * @param g Contexte graphique pour le dessin
     * @param decalageX Decalage horizontal pour centrer l'image
     * @param decalageY Decalage vertical pour centrer l'image
     */
    private void dessinerCheminEtPoints(Graphics g, int decalageX, int decalageY) {
        if (displayedImage == null || graph == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Calculer les facteurs d'echelle
        double echelleX = (double) displayedImage.getWidth() / originalImage.getWidth();
        double echelleY = (double) displayedImage.getHeight() / originalImage.getHeight();
        
        // Dessiner le chemin si existant
        if (chemin != null && !chemin.isEmpty()) {
            g2d.setColor(new Color(0, 255, 0, 180)); // Vert semi-transparent
            g2d.setStroke(new BasicStroke(3));
            
            for (int i = 0; i < chemin.size() - 1; i++) {
                Sommet s1 = graph.getSommet(chemin.get(i));
                Sommet s2 = graph.getSommet(chemin.get(i + 1));
                
                if (s1 != null && s2 != null) {
                    int x1 = decalageX + (int) (s1.getCoordonnee().getColonne() * echelleX);
                    int y1 = decalageY + (int) (s1.getCoordonnee().getLigne() * echelleY);
                    int x2 = decalageX + (int) (s2.getCoordonnee().getColonne() * echelleX);
                    int y2 = decalageY + (int) (s2.getCoordonnee().getLigne() * echelleY);
                    
                    g2d.drawLine(x1, y1, x2, y2);
                }
            }
        }
        
        // Dessiner le point de depart (rouge)
        if (depart != -1) {
            Sommet s = graph.getSommet(depart);
            if (s != null) {
                int x = decalageX + (int) (s.getCoordonnee().getColonne() * echelleX);
                int y = decalageY + (int) (s.getCoordonnee().getLigne() * echelleY);
                
                g2d.setColor(Color.RED);
                g2d.fillOval(x - 8, y - 8, 16, 16);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x - 8, y - 8, 16, 16);
                
                // Dessiner un D pour depart
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString("D", x - 3, y + 4);
            }
        }
        
        // Dessiner le point d'arrivee (vert)
        if (arrivee != -1) {
            Sommet s = graph.getSommet(arrivee);
            if (s != null) {
                int x = decalageX + (int) (s.getCoordonnee().getColonne() * echelleX);
                int y = decalageY + (int) (s.getCoordonnee().getLigne() * echelleY);
                
                g2d.setColor(new Color(0, 180, 0));
                g2d.fillOval(x - 8, y - 8, 16, 16);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x - 8, y - 8, 16, 16);
                
                // Dessiner un A pour arrivee
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString("A", x - 3, y + 4);
            }
        }
    }
    
    /**
     * Execute l'algorithme selectionné.
     * Gere Dijkstra et A* avec differentes heuristiques.
     * S'execute dans un thread separe pour ne pas bloquer l'interface.
     */
    private void executerAlgorithme() {
        if (graph == null || depart == -1 || arrivee == -1) {
            resultArea.setText("Selectionnez d'abord le depart et l'arrivee !");
            return;
        }
        
        if (depart == arrivee) {
            resultArea.setText("Le depart et l'arrivee sont identiques !");
            return;
        }
        
        String algorithme = algoChoice.getSelectedItem();
        String heuristique = heuristiqueChoice.getSelectedItem();

        graph = graph.clone();//On clone le graphe pour travailler sur un autre graphe.
        
        resultArea.setText("Execution de " + algorithme + 
                          (algorithme.equals("A*") ? " (" + heuristique + ")" : "") + "...");
        
        new Thread(() -> {
            long debut = System.currentTimeMillis();
            
            try {
              
                if ("Dijkstra".equals(algorithme)) {
                    djikstra = new Djikstra(depart, arrivee, graph);
                    djikstra.launch();
                    chemin = djikstra.getChemin();
                    afficherResultats("Dijkstra", djikstra.getCout(), djikstra.getNbSommetsVisites(), debut);
                 
                    
                } else if ("A*".equals(algorithme)) {
                    sommetArrivee = graph.getSommet(arrivee);
                     
                    
                    switch (heuristique) {
                        case "Manhattan":
                            h = new HeuristiqueManhattan(graph, sommetArrivee);
                            break;
                        case "Euclidienne":
                            h = new HeuristiqueEuclidienne(graph, sommetArrivee);
                            break;
                        case "Octile":
                            h = new HeuristiqueOctile(graph, sommetArrivee);
                            break;
                    }
                    
                    aEtoile = new AEtoile(depart, arrivee, graph, h);
                    aEtoile.launch();
                   
                    chemin = aEtoile.getChemin();
                    afficherResultats("A* (" + heuristique + ")", aEtoile.getCout(), aEtoile.getNbSommetsVisites(), debut);
                }
                
            } catch (Exception e) {
                EventQueue.invokeLater(() -> {
                    resultArea.setText("Erreur lors de l'execution: " + e.getMessage());
                });
            }
        }).start();
    }
    
    /**
     * Affiche les resultats de l'execution de l'algorithme.
     * Factorise le code commun a Dijkstra et A* pour eviter la duplication.
     * @param nomAlgo Nom de l'algorithme execute
     * @param cout Cout total du chemin trouve
     * @param sommetsVisites Nombre de sommets visites pendant l'execution
     * @param debut Timestamp du debut de l'execution (pour calcul du temps)
     */
    private void afficherResultats(String nomAlgo, double cout, int sommetsVisites, long debut) {
        EventQueue.invokeLater(() -> {
            long fin = System.currentTimeMillis();
            long tempsExecution = fin - debut;
            
            if (cout != Double.POSITIVE_INFINITY ) {//Si un cout a bien été calculé, on procède à l'affichage.
                coutLabel.setText(String.format("%.2f", cout));
                sommetsLabel.setText(sommetsVisites + "/" + graph.getNombreSommets());
                // tempsLabel.setText(tempsExecution + " ms");
                
                double tauxVisites = (sommetsVisites * 100.0) / graph.getNombreSommets(); // Le taux est le nombre de sommets visités par rapport au nombre de sommet total du graphe(nombre de pixels total de l'image)
                resultArea.setText(nomAlgo + " termine en " + tempsExecution + " ms\n" +
                                 "Chemin trouve: " + chemin.size() + " sommets\n" +
                                 "Cout total: " + String.format("%.2f", cout) + 
                                 "\nSommets visites: " + sommetsVisites + 
                                 "/" + graph.getNombreSommets() +
                                 "\nTaux: " + String.format("%.1f", tauxVisites) + "%");
            }else {
                resultArea.setText(nomAlgo + " termine en " + tempsExecution + " ms\n" +
                                 "Aucun chemin trouve entre les points selectionnes.");
                coutLabel.setText("-");
                sommetsLabel.setText("-");
                tempsLabel.setText(tempsExecution + " ms");
            }
            imageCanvas.repaint();
        });
    }
    
    /**
     * Reinitialise l'interface a son etat initial.
     * Efface la selection, le chemin et les resultats precedents.
     */
    private void reinitialiser() {
        depart = -1;
        arrivee = -1;
        chemin = null;
        
        coutLabel.setText("-");
        sommetsLabel.setText("-");
        
        
        
        
        if (graph != null) {
            infoArea.setText("Image chargée (" + graph.getLigne() + "x" + graph.getColonne() + ")\n" +
                           "Cliquez sur l'image pour selectionner le point de depart (rouge).");
            resultArea.setText("Selection reinitialisee\n" +
                             "Graphe: " + graph.getNombreSommets() + " sommets\n" +
                             "Selectionnez un nouveau depart et arrivee");
        } else {
            infoArea.setText("Selectionnez une image pour commencer.");
            resultArea.setText("Aucune image chargee.");
        }
        
        imageCanvas.repaint();
    }
    
   
}
