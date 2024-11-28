import java.io.*;
import java.net.*;

public class Sender {

    /**
     * Instance de la classe HDLC pour gérer les communications. 
     */
    private final HDLC hdlc;
    
    /** 
     * Trames de 256 octets - 6 octets fixes (2 flags + type + num + CRC) = 250 octets. Pourrait changer de valeur.
     */
    private static final int FRAME_SIZE_MAX = 250;
    
    /** 
     * Temps d'attente de 3 secondes pour le retour de Receiver. 
     */
    private static final int TIMEOUT = 3000;
    
    /** 
     * Numéro de la trame en cours pour le compteur de 0 à 7 (sur 3 bits).  
     */    
    private byte frameNum = 0;
    
    /**
     * Constructeur de Sender.
     * Initialise une connexion avec Receiver avec un socket serveur.
     * @param machineName la machine de destination   
     * @param port le port de destination
     * @throws IOException
     */
    public Sender(String machineName, int port) throws IOException {
        System.out.println("Connexion en cours avec le port " + port + " de " + machineName + "...");
        Socket socket = new Socket(machineName, port);
        this.hdlc = new HDLC(socket);
        System.out.println("Connexion établie avec le port " + port + " de " + machineName + " !");
    }
  
    /**
     * Méthode pour lire un fichier.
     * @param fileName le fichier à lire passé en paramètre dans la ligne de commande
     * @throws IOException
     */
    public static void readFile(String fileName) throws IOException {
        
        // Ouvrir le fichier pour lecture
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        
        // Lire les données tant qu'il en reste dans le fichier
        String line;
        while ((line = reader.readLine()) != null) {

        }
        
        reader.close();    
    }   
}
