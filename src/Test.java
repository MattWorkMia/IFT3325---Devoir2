import java.io.IOException;
import java.util.Random;

public class Test {

    private static final Random rand = new Random();

    /**
     * Main pour tester les classes Sender et Receiver.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Démarrage des tests HDLC...");

        // 1. Test simple d'envoi et de réception sans erreur
        //testEnvoiReception();

        // 3. Test avec erreur d'un bit
        testErreurBit();

        System.out.println("Tous les tests terminés.");
    }

    /**
     * Test simple : Envoi et réception sans erreur.
     */
    public static void testEnvoiReception() throws IOException {
        System.out.println("=== Test Envoi/Réception ===");

        String fichierTest = "../test.txt";

        int port = 12345; // Port local
        new Thread(() -> {
            try {
                Receiver receiver = new Receiver(port);
                receiver.start();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Sender sender = new Sender("localhost", port);
        sender.createFrame(fichierTest);

        System.out.println("Test simple terminé.");
    }

    /**
     * Test avec une erreur sur un bit d'une trame.
     */
    public static void testErreurBit() throws IOException {
        System.out.println("=== Test avec Erreur de Bit ===");

        String fichierTest = "test.txt";

        int port = 12347; // Nouveau port pour ce test
        new Thread(() -> {
            try {
                Receiver receiver = new Receiver(port) {
                    @Override
                    public Trame recoitTrame() {
                        Trame trame = hdlc.recoitTrame();
                        // Introduce a single-bit error in the frame
                        if (trame != null && rand.nextBoolean()) {
                            System.out.println("Receiver: Introducing single-bit error.");
                            return Test.InverseBitAleatoire(trame);
                        }
                        return trame;
                    }
                };
                receiver.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        Sender sender = new Sender("localhost", port);
        sender.createFrame(fichierTest);

        System.out.println("Test erreur de bit terminé.");
    }



    /**
     * Inverse un bit aléatoire dans une trame
     * @param trame La trame à modifier.
     * @return La trame initiale avec une erreur introduite
     */
    public static Trame InverseBitAleatoire(Trame trame) {
        byte[] donne = trame.getDonne();
        if (donne.length == 0) return trame;

        int byteIndex = rand.nextInt(donne.length); // Byte au hasard
        donne[byteIndex] ^= (1 << rand.nextInt(8)); // Inversion du bit dans le byte choisi
        return new Trame(trame.getType(), trame.getNum(), donne);
    }

    /**
     * Simule une perte de trame.
     * @return null pour simuler une perte de trame.
     */
    public static Trame PerteTrame() {
        return null;
    }

    /**
     * Affiche 
     * @param frame La trame à afficher.
     * @param role  Le rôle (Émetteur ou Récepteur).
     */
    public static void afficheTrame(Trame trame, String role) {
        if (trame == null) {
            System.out.println(role + ": Trame perdue.");
        }
        else{
            System.out.println(role + ": ");
            System.out.println(trame);
        }
    }


}

    