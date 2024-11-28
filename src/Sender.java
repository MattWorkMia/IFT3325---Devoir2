import java.io.*;
import java.net.*;

/**
 * Sender lit le fichier source, crée des trames et envoie ces trames à Receiver sur le socket.
 * Sender reçoit les ACK et REJ de Receiver et ré-envoie les données au besoin.
 */
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
           public static void createFrame(String fileName) throws IOException {
        
        // Ouvrir le fichier pour lecture
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        
        // Lire les données tant qu'il en reste dans le fichier
        String line;
        while ((line = reader.readLine()) != null) {
            sendFrame(line.getBytes());
        }
        
        reader.close();
        
        // Ajout d'une trame pour la fin de communication
        Trame endFrame = new Trame(TrameType.F, frameNum++, new byte[0]);
        hdlc.envoieTrame(endFrame);

        // Fermer la connexion après l'envoi de la trame de fin
        hdlc.fermeConnection();
    }
    
    public void sendFrame(byte[] data) throws IOException {
        int dataLength = data.length;
        int position = 0;

        while (position < dataLength) {
            int chunkSize = Math.min(MAX_FRAME_SIZE, dataLength - position);
            byte[] chunkData = new byte[chunkSize];
            System.arraycopy(data, position, chunkData, 0, chunkSize);

            // Crée une trame d'information et envoie la trame
            Trame trame = new Trame(TrameType.I, numTrame++, chunkData);
            hdlc.envoieTrame(trame);

            // Attente de l'ACK ou REJ
            long startTime = System.currentTimeMillis();
            boolean ackReceived = false;
            while (System.currentTimeMillis() - startTime < TIMEOUT) {
                Trame ack = hdlc.receiveFrame();
                if (ack != null && ack.getType() == TrameType.A && ack.getNum() == trame.getNum()) {
                    ackReceived = true;
                    break;
                } else if (ack != null && ack.getType() == TrameType.R) {
                    // Si REJ est reçu, envoyer la trame à nouveau
                    System.out.println("REJ pour la trame " + trame.getNum() + ", renvoi...");
                    hdlc.envoieTrame(trame);
                }
            }

            if (!ackReceived) {
                System.out.println("Délai dépassé, nouvel envoi de la trame " + trame.getNum());
                hdlc.envoieTrame(trame);  // Renvoi la trame si le délai a expiré
            }

            position += chunkSize;
        }
    }

        /**
         * main pour mes tests Sender et affichage, à déplacer ou supprimer
         */
        public static void main(String[] args) throws IOException {
        
        if (args.length != 4) {
            System.out.println("Vous devez entrer 4 paramètres dans la ligne de commande.");
            System.out.println("Format attendu : java Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <Valeur_GBN>");
        }
        
        /*
         * Récupération des paramètres passés dans la ligne de commande
         */
        String machineName = args[0];
        int port = Integer.parseInt(args[1]);
        String fileName = args[2];
        int gbnValue = Integer.parseInt(args[3]);
          
        /*
         * Appel avec les paramètre saisi en ligne de commande
         */
        readFile(fileName);
        System.out.println("Nom de la machine : " + machineName);
        System.out.println("Num du port : " + port);
        System.out.println("Nom du fichier : " + fileName);
        System.out.println("Valeur du bit GBN : " + gbnValue);
    }
}
