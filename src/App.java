/**
 * @author SAIFIDINE Dayar
 * @version 1.1
 */
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
/** 
 * Point d'entree principal de l'application.
 * Initialise l'interface graphique pour la recherche de chemin dans les images.
 */
public class App {
    /**
     * Methode principale qui lance l'application .
     * Cree le dossier ressources si necessaire et initialise l'interface graphique. 
     * @param args Arguments de ligne de commande (non utilises)
     */
    public static void main(String[] args) {
        // Creation du dossier ressources si necessaire
        String repertoireCourant = new java.io.File("").getAbsolutePath();
        String cheminRessources = repertoireCourant + java.io.File.separator + 
                                 "CodeMiniProjet" + java.io.File.separator + 
                                 "ressources";
        java.io.File dossierRessources = new java.io.File(cheminRessources);
        
        if (!dossierRessources.exists()) {
            dossierRessources.mkdirs();
        }
        
        // Lancement de l'interface graphique
        EventQueue.invokeLater(() -> {
            try {
                InterfaceGraphique interfaceGraphique = new InterfaceGraphique();
            } catch (Exception e) {
                // En cas d'erreur, affichage d'une interface d'erreur simple
                Frame fenetreErreur = new Frame("Erreur");
                fenetreErreur.setLayout(new BorderLayout());
                
                Panel panelMessage = new Panel(new BorderLayout());
                panelMessage.add(new Label("Erreur lors du demarrage:", Label.CENTER), BorderLayout.NORTH);
                TextArea detailsErreur = new TextArea(e.getMessage(), 3, 40);
                detailsErreur.setEditable(false);
                panelMessage.add(detailsErreur, BorderLayout.CENTER);
                
                Button boutonFermer = new Button("Fermer");
                boutonFermer.addActionListener(ev -> System.exit(1));
                panelMessage.add(boutonFermer, BorderLayout.SOUTH);
                
                fenetreErreur.add(panelMessage, BorderLayout.CENTER);
                fenetreErreur.setSize(400, 150);
                fenetreErreur.setLocationRelativeTo(null);
                fenetreErreur.setVisible(true);
            }
        });
    }
}
