import java.io.*;
import java.net.*;


/**
 * Receiver reçoit et traite les trames envoyer par l'emetteur sur le socket
 */
public class Receiver {

    /** Instance de la classe HDLC pour gérer les communications. */
    private final HDLC hdlc;

    /**
     * Constructeur de Receiver.
     * Initialise une connexion avec un socket serveur.
     * @param port le port d'écoute
     * @throws IOException
     */
    public Receiver(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("En attente de connexion...");
        Socket socket = serverSocket.accept();
        System.out.println("Connexion établie !");
        this.hdlc = new HDLC(socket);
    }

    /**
     * Commence a recevoir et traiter les trames
     * @throws IOException 
     */
    public void start() throws IOException {
        byte numTrameAttendu = 0; //On initialise a 0 le numero de la premiere trame attendu

        while (true) {
            Trame trame = hdlc.receiveTrame();

            if (trame == null) continue;

            if (trame.getType() == TrameType.F) { //La communication est fini
                System.out.println("Fin de communication reçue."); 
                break;
            }

            if (checkTrame(trame, numTrameAttendu)) { //On vérifie que la trame est valide

                Trame ackTrame = new Trame(TrameType.A, trame.getNum(), new byte[0]);
                hdlc.envoieTrame(ackTrame); //Envoie de l'ACK
                System.out.println("ACK envoyé pour la trame : " + trame.getNum());
                //On incremente le numero de trame attendu (modulo 8 pour limiter a 3 bits la numerotation)
                numTrameAttendu = (byte) ((numTrameAttendu + 1) % 8);
            } else {
                System.out.println("Trame invalide, envoi de REJ : " + trame.getNum());
                Trame rejTrame = new Trame(TrameType.R, trame.getNum(), new byte[0]); 
                hdlc.envoieTrame(rejTrame); //Envoie de REJ
            }
        }

        hdlc.fermeConnection();
    }

    /**
     * Vérifie si le CRC est correct et si le numéro de trame correspond à celui attendu.
     * @param trame la trame à valider.
     * @param numTrameAttendu le numéro de trame attendu.
     * @return true si la trame est valide, false sinon.
     */
    private boolean checkTrame(Trame trame, byte numTrameAttendu) {
        return trame.getCrc() == trame.calculerCRC() && trame.getNum() == numTrameAttendu;
    }
}