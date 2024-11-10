import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * HDLC représente une connexion utilisant le protocole HDLC pour l'envoi et la réception de trames.
 */
public class HDLC {
    /** Le socket pour la connexion */
    private Socket socket;

    /** Flux de données pour lire les données*/
    private DataInputStream inputStream;

    /** Flux de données pour écrire les données */
    private DataOutputStream outputStream;

    /**
     * Constructeur de la  HDLC.
     * Initialise les flux d'entrée et de sortie à partir du socket donné.
     * @param socket le socket utilisé pour la connexion HDLC
     */
    public HDLC(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Envoie une trame en écrivant sur l'output stream
     * @param trame la trame à envoyer
     */
    public void envoieTrame(Trame trame) throws IOException {
        byte[] trameBytes = trame.trame_to_tab();
        outputStream.write(trameBytes);
        System.out.println("Envoie: " + trame);
    }

    /**
     * Reçoit une trame
     * Lit les données sur l'input stream et les transforme en une trame.
     * @return la trame reçue ou null si aucun byte n'est lu
     */
    public Trame receiveFrame() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesLus = inputStream.read(buffer);
        if (bytesLus == -1) return null; //Aucun byte lu on retourne null
        return Trame.donne_to_Trame(Arrays.copyOf(buffer, bytesLus));
    }

    /**
     * Affiche un message pour l'ACK d'une trame
     * @param trame la trame pour laquelle l'ACK a été reçu
     */
    public void str_ACK(Trame trame) {
        System.out.println("Reçu ACK de la trame " + trame.getNum());
    }

    /**
     * Affiche un message pour le REJ d'une trame
     * @param trame la trame pour laquelle le REJ a été reçu
     */
    public void str_REJ(Trame trame) {
        System.out.println("Reçu REJ de la trame " + trame.getNum());
    }

    /** Ferme la connexion */
    public void fermeConnection() throws IOException {
        socket.close();
    }
}
