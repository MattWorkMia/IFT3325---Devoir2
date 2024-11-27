import java.util.Random;

public class Test {

    private static final Random rand = new Random();

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

    