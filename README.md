Étape 1 : Analyse et Conception
Étudie le protocole HDLC et Go-Back-N (GBN) :

Familiarise-toi avec le fonctionnement du protocole HDLC, particulièrement les éléments comme les trames, accusés de réception (RR), et rejets (REJ).
Comprends le mécanisme de Go-Back-N, où en cas de perte ou d'erreur, toutes les trames à partir de la trame erronée sont réémises.
Diagramme de classe :

Prépare un diagramme de classe avec les principales classes : Sender (émetteur), Receiver (récepteur), Frame (trame), et Test.
Pour chaque classe, identifie ses attributs et méthodes principaux.
Définition des rôles des classes :

Sender : Gère la lecture de fichier, l’envoi de trames, et le traitement des accusés de réception.
Receiver : Reçoit les trames, vérifie les erreurs, et envoie les réponses appropriées (RR ou REJ).
Frame : Représente une trame, avec ses champs (Flag, Type, Num, Data, CRC, Flag).
Test : Simule des erreurs et pertes de trames, et affiche les informations pour contrôler le flux.
Étape 2 : Conception de la classe Frame et Gestion des Trames
Structure de la trame :

Définit les champs de la trame (par exemple, flag, type, num, data, crc).
Crée une méthode pour assembler les trames et une pour les décomposer à la réception.
Bit Stuffing :

Implemente le bit stuffing sur tous les champs sauf le flag.
Ajoute une méthode pour réaliser le bit stuffing avant l'envoi et une pour l’inverser à la réception.
Calcul CRC-CCITT :

Recherches le calcul du CRC-CCITT et définis une méthode pour le calculer sur les champs Type, Num, et Données.
Mets à jour la trame avec le champ CRC avant de l’envoyer.
Étape 3 : Mise en Place de la Classe Sender (Émetteur)
Lecture du fichier et fragmentation en trames :

Prépare une méthode pour lire les données du fichier texte et diviser les données en trames.
Stocke les trames pour les envoyer avec le numéro de séquence associé.
Envoi des trames :

Crée une méthode pour envoyer les trames par socket. Assure-toi que chaque trame a un numéro de séquence (sur 3 bits) et utilise un timeout pour gérer la perte de trame (par défaut 3 secondes).
Gestion des réponses (RR et REJ) :

Mets en place un mécanisme pour écouter les réponses (RR ou REJ) et identifier si des trames doivent être réémises.
En cas de REJ, réémets toutes les trames à partir de la trame rejetée.
Étape 4 : Mise en Place de la Classe Receiver (Récepteur)
Réception des trames :
Établis une méthode pour recevoir les trames, retirer le bit stuffing, et vérifier le CRC.
Gestion des erreurs :
Crée une méthode pour vérifier les erreurs dans les trames (par exemple, CRC incorrect).
En cas d’erreur, envoie un REJ ; sinon, envoie un accusé de réception (RR) pour la trame valide.
Étape 5 : Gestion des Erreurs et Pertes avec la Classe Test
Simuler des erreurs de transmission :

Implemente une méthode dans Test pour inverser aléatoirement des bits dans les trames envoyées.
Simuler des pertes de trame :

Ajoute des fonctionnalités pour simuler la perte de trame et la perte de réponses en omettant d’envoyer certaines trames ou réponses.
Affichage des trames :

Prévois un affichage détaillé pour chaque trame (émise et reçue) avec ses détails (numéro, type, données, CRC, etc.).
Étape 6 : Rapport et Tests Finaux
Rédaction du rapport :

Rédige une brève description de chaque classe et méthode, en incluant les paramètres, les objectifs et les résultats.
Tests finaux :

Exécute des tests complets sous Linux (compatibles avec la plateforme du DIRO).
Vérifie le bon fonctionnement de l’émetteur et du récepteur, et que les erreurs et pertes sont bien gérées.
Présentation :

Prépare une présentation en t’assurant d’expliquer le diagramme de classe, les mécanismes de HDLC et de Go-Back-N, ainsi que la gestion des erreurs.
