/**
 * @author SAIFIDINE Dayar
 * @version 1.0
 */
/**
 * Implémente l'heuristique octile.
 */
public class HeuristiqueOctile implements InterfaceHeuristique{
     /**
     * Le graphe qui contient les sommets sur lequels l'heuristique sera appliquée.
     */
    private final Graph graph;
    /**
     * Le sommet d'arrivée.
     */
    private final Sommet arrivee;
     /**
     * Le constructeur.
     * @param graph Le graphe qui contient les sommets à définir l'heuristique.
     * @param arrivee Le sommet d'arrivée.
     */
    public HeuristiqueOctile(Graph graph, Sommet arrivee){
        this.graph = graph;
        this.arrivee = arrivee;
    }
    /**
     * Implémente la fonction h(n) et affecte le cout des sommets (les pixels) du graphe jusqu'au sommet d'arrivée. Pour en savoir plus sur la formule appliquée il est conseillé de lire le compte rendu.
     */
    @Override
    public void appliquer(){
        int xA, yA;
        int xB=arrivee.getCoordonnee().getColonne(), yB=arrivee.
        getCoordonnee().getLigne();

        double dx, dy, D, D2, h;
        for(Sommet s : graph.getSommets()){
            xA = s.getCoordonnee().getColonne();
            yA = s.getCoordonnee().getLigne();
            dx = Math.abs(xB - xA);
            dy = Math.abs(yB - yA);
            D = s.getCout();
            D2 = Math.sqrt(2)*D;//Pour la preuve voir le compte rendu
            h = D*(dx + dy) + (D2-2*D)*Math.min(dx,dy);
            s.setHeuristique(h);// Mise à jour de l'heuristique
        }
    }
}
