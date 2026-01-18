=============================================
PROJET ALGORITHMIQUE AVANCÉE - SCÉNARIO 2
Graphes et Image (Dijkstra et modélisation)
=============================================

Auteur : SAIFIDINE Dayar
Date : Décembre 2025
Version : 1.0

## Description
Ce projet implémente la modélisation d'images en graphes et la recherche
de plus court chemin entre deux pixels à l'aide des algorithmes de
Dijkstra et A*.

## Structure du projet

CodeMiniProjet/
├── src/          # Code source Java
├── ressources/   # Images pour tests
├── bin/          # Fichiers .class après compilation
└── README.txt    # Ce fichier

## Compilation sur Linux ou Windows
1. Ouvrir un terminal dans le dossier Code_MiniProjet (cd CodeMiniProjet)
2. Compiler : javac -d bin src/*.java

## Exécution sur Linux ou Windows
Il faut avant tout compiler les fichiers. Par la suite, lancez la commande ci-dessous : 
1. Depuis le dossier Code_MiniProjet :
   java -cp bin App
   
## Utilisation du logiciel 
1. Lancer l'application
2. Cliquer sur "CHOISIR UNE IMAGE"
3. Sélectionner une image dans le dossier ressources/ ou une autre image de votre choix qui respecte les dimensions explicitées ci-dessous.

4. Cliquer sur l'image pour choisir :
   - Point de départ (rouge)
   - Point d'arrivée (vert)
5. Sélectionner l'algorithme :
   - Dijkstra : recherche complète
   - A* : recherche guidée (choisir heuristique)
6. Cliquer sur "EXECUTER L'ALGORITHME"
7. Visualiser le chemin en vert sur l'image
Par la suite, vous pouvez réinitialiser l'affichage pour recommencer la même procédure ou bien choisir une autre image.

Attention  : Si vous recliquez une deuxième fois sur "EXECUTER L'ALGORITHME" sans avoir réinitialiser, le logiciel, peut dans certains cas fournir des données erronnées. Il est fortement recommandé de réinitialiser l'affichage avant de faire une nouvelle recherche. 

## Formats d'images supportés
- JPG, JPEG
- PNG
- GIF
- BMP

## Taille d'image recommandée
- Maximum : 1000×1000 pixels (<=1000 0000 pixels)
- Optimal : 300×300 à 700×500 pixels

## Algorithme implémentés
1. Dijkstra 
2. A* avec trois heuristiques :

   - Manhattan (distance en L)
   - Euclidienne (distance directe)
   - Octile (optimale pour 8 directions)

## Dépendances du projet
- Java Runtime Environment 8+
- Bibliothèques standards Java (AWT, ImageIO)

## Résolution des problèmes courants

- "Image trop grande" : Choisissez une image qui réponds aux exigences requis : bon format et surtout bonne taille. Pour l'affichage, celle-ci est redimensionnée automatiquement.
- "Aucun chemin trouvé" : Points non connectés
- Erreur mémoire : Réduire la taille de l'image
- Fichier non trouvé : Votre image est erronée. Utilisez les images se situant dans le dossier ressources/

## Contact
Pour toute question : dayar.saifidine@etu.u-paris.fr

## LICENCE
Projet académique - Université Paris Cité
