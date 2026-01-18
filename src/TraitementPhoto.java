/**
 * @author SAIFIDINE Dayar
 * @version 1.1
 * 
 * Classe responsable du traitement des images pour generer un graphe correspondant.
 */
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class TraitementPhoto {
    /**
     * Graphe genere a partir de l'image traitee.
     * Contient un sommet pour chaque pixel de l'image.
     */
    private Graph graph;
    
    /**
     * Fichier image a traiter.
     */
    private File fichier;
    
    /**
     * Representation en memoire de l'image, utilisee pour acceder aux pixels.
     * BufferedImage est utilise car il permet une manipulation directe des pixels.
     */
    private BufferedImage image;
    
    /**
     * Constructeur de la classe TraitementPhoto.
     * @param fichier la photo qui sera traitée pour en retirer un graphe.
     * @throws IllegalArgumentException Si le fichier n'existe pas ou ne peut etre lu
     * @throws IOExceptio Si une autre erreur survient lors de la lecture
     */
    public TraitementPhoto(File fichier) throws IOException, IllegalArgumentException{
        this.fichier = fichier;
         
        try {
           
            this.image = ImageIO.read(this.fichier);
             
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erreur de chargement de l'image: fichier non reconnu.");
        } 
        catch (IOException e) {
            throw new IllegalArgumentException("Erreur de chargement de l'image");
        }
    }
    
    /**
     * Methode principale de traitement de l'image.
     * Convertit l'image en graphe ou chaque pixel devient un sommet
     * et les relations de voisinage (4-connexite) deviennent des arretes.
     * Le poids d'une arrete correspond a la difference d'intensite de couleur
     * entre les deux pixels adjacents.
     */
    public void launch() throws IllegalArgumentException, RuntimeException{
        if (this.image == null) {
            throw new IllegalStateException("Aucune image chargee. Verifiez le chemin du fichier.");
        }
        
        try {
            Raster raster = this.image.getRaster();
            int nb_pixels_h = raster.getHeight();    // Nombre de pixels en hauteur (lignes)
            int nb_pixels_w = raster.getWidth();     // Nombre de pixels en largeur (colonnes)
            int nb_total_pixels = nb_pixels_w * nb_pixels_h;
            
            // Verification de la taille de l'image (eviter les images trop grandes)
            if (nb_total_pixels > 1000000) { // Limite a 1 million de pixels
                throw new IllegalStateException("Image trop grande (" + nb_total_pixels + 
                                              " pixels). Maximum: 1 million de pixels.");
            }
            
            // Creation du graphe et mise a jour des dimensions
            graph = new Graph();
            graph.setColonne(nb_pixels_w);
            graph.setLigne(nb_pixels_h);
            
            // Chaque pixel de l'image devient un sommet du graphe
            // L'etiquette du sommet est calculee comme: ligne * largeur + colonne
            // Le cout du sommet correspond a l'intensite de couleur RGB du pixel adapté à la sensibilité de l'oeil humain
            Coordonnee tamp_coordonnee;
            double cout;
            int r, g,b;
            for (int ligne = 0; ligne < nb_pixels_h; ligne++) {
                for (int colonne = 0; colonne < nb_pixels_w; colonne++) {
                    tamp_coordonnee = new Coordonnee(ligne, colonne);
                    
                    int rgb = image.getRGB(colonne, ligne);
                    r = (rgb >> 16) & 0xFF; // Composante rouge (0-255)
                     g = (rgb >> 8) & 0xFF;  // Composante verte (0-255)
                    b = rgb & 0xFF;         // Composante bleue (0-255)

                    // Formule selon sensibilité de l'oeil humain
                    cout = 0.299 * r + 0.587 * g + 0.114 * b;//Valeur entre 0 (noir) et 255 (blanc)

                    graph.ajouterSommet(cout, tamp_coordonnee);
                }
            }
            
            Sommet sommet2;
            // Directions des voisins: droite, gauche, bas, haut
            int[][] tab_adjacence = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            double poids_arrete;
            int x1, y1, x2, y2, etiquette2;
            
            for (Sommet sommet1 : graph.getSommets()) {
                x1 = sommet1.getCoordonnee().getLigne();
                y1 = sommet1.getCoordonnee().getColonne();
                
                // Parcours des 4 voisins possibles
                for (int[] tab_adj : tab_adjacence) {
                    x2 = x1 + tab_adj[0];
                    y2 = y1 + tab_adj[1];
                    etiquette2 = x2 * nb_pixels_w + y2;
                    
                    // Verification que le voisin existe dans l'image
                    if (graph.sommetExiste(etiquette2)) {
                        // Si le sommet1 est un voisin en 4-connexite, on cree l'arrete
                        sommet2 = graph.getSommet(etiquette2);
                        // Poids = valeur absolue de la difference d'intensite
                        poids_arrete = Math.abs(sommet1.getCout() - sommet2.getCout());
                        graph.ajouterArrete(sommet1.getEtiquette(), sommet2.getEtiquette(), poids_arrete);
                    }
                }
            }
            
        } catch (RuntimeException e) {
            throw new RuntimeException("Erreur lors du traitement de l'image: " + e.getMessage());
        }
    }
    
    /**
     * Retourne le graphe genere a partir de l'image.
     * @return Le graphe representant l'image traitee
     */
    public Graph getGraph() {
        if (graph == null) {
            throw new IllegalStateException("Graphe non initialise. Appelez d'abord launch().");
        }
        return graph;
    }
}
