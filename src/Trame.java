import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TrameType représente les types de trames, chacun avec un code byte 
 * correspondant à un caractère ASCII
 */
enum TrameType {
    I((byte) 'I'),   //trame d’information 
    C((byte) 'C'),   //demande de connexion 
    A((byte) 'A'),   //accusé de réception (RR)
    R((byte) 'R'),   //rejet de la trame Num et de toutes celles envoyées après 
    F((byte) 'F'),   //fin de la communication
    P((byte) 'P');   //trame avec P bit, équivalente à P bit.

    /**
     * Le code byte associé au type de la trame
     */
    private final byte code;

    /**
     * Constructeur de  TrameType.
     * @param code le code byte associé au type de la trame
     */
    TrameType(byte code) {
        this.code = code;
    }

    /**
     * Accesseur de code
     *
     * @return le code byte associé au type de la trame.
     */
    public byte getCode() {
        return code;
    }

    /**
     * Récupère le type de trame correspondant à un code byte
     * @param code le code byte pour lequel on veut trouver le type de trame
     * @return le type de trame correspondant au code, 
     *         ou null si aucun correspondance
     */
    public static TrameType fromCode(byte code) {
        for (TrameType type : TrameType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}

/**
 * Trame représente une trame du protocole HDLC
 */
class Trame {
    /**
     * Le polynôme générateur CRC-CCITT (x16 + x12 + x5 + 1)
     */
    private static final short CRC_CCIT = 0x1021;

    /**
     * Le flag indiquant le début et la fin de la trame (01111110)
     */
    private static final byte FLAG = 126;

    /**
     * Le numéro de la trame
     */
    private final byte num;

    /**
     * Les données de la trame
     */
    private final byte[] donne;

    /**
     * Le checksum calculé en utilisant CRC
     */
    private final short crc;

    /**
     * Le type de la trame, avec enum TrameType
     */
    private final TrameType type;

    /**
     * Constructeur de Trame.
     * Initialise une nouvelle trame avec le type, le numéro, les données
     * et calcule le CRC.
     * @param type  le type de la trame.
     * @param num   le numéro de la trame.
     * @param donne les données de la trame.
     */
    Trame(TrameType type, byte num, byte[] donne) {
        this.type = type;
        this.num = num;
        this.donne = donne;
        this.crc = calculerCRC();
    }

    /**
     * Calcule le CRC pour la trame actuelle en utilisant le protocole CRC-CCITT.
     * Le checksum est calculé sur les champs Type, Num et Données.
     * @return le CRC calculé sous forme de short.
     */
    public final short calculerCRC() {
        //on prépare de quoi accueuillir le type, le numero et les données
        byte[] donne_checksum = new byte[donne.length + 2]; 
        donne_checksum[0] = type.getCode(); //on recupere le type
        donne_checksum[1] = num; //on recupere le numero de la trame
        System.arraycopy(donne, 0, donne_checksum, 2, donne.length);
        
        
        int checksum = 0x0000; // on initialise a 0x16

        //On itère chaque byte des données utilisé pour calculer le checksum
        for (byte b : donne_checksum) {
            //On XOR checksum avec le byte b auquel on a rajouté 8 zéros pour aligner les bits forts
            checksum ^= (b << 8); 

            //on itère chaque bit du byte b
            for (int i = 0; i < 8; i++) {
                //On check si le bit fort de checksum est 1
                //Si 1 : On decale le checksum de 1 bit vers la gauche puis on XOR avec le polynome CRC-CCIT
                //Si 0 : On decale le checksum de 1 bit vers la gauche
                if ((checksum & 0x8000) != 0)
                    checksum = (checksum << 1) ^ CRC_CCIT;
                else
                    checksum <<= 1;
            }
        }
        //checksum & 0xFFFF nous donne les 16 bits faibles correspondants au checksum
        //(short) pour convertir en short
        return (short) (checksum & 0xFFFF);
    }

    /**
     * Convertit la trame actuelle en un tableau de bytes, prêt à être transmis.
     * @return la trame sous forme de tableau de bytes.
     */
    public byte[] trame_to_tab() {
        byte[] donne_stuffed = bitstuffing(donne);
        byte[] trame = new byte[6 + donne_stuffed.length];

        trame[0] = FLAG; //ajoute le flag de début
        trame[1] = type.getCode(); 
        trame[2] = num; 

        System.arraycopy(donne_stuffed, 0, trame, 3, donne_stuffed.length);

        trame[3 + donne.length] = (byte) ((crc >> 8) & 0xFF); //les 8 bits de poid fort du crc
        trame[4 + donne.length] = (byte) (crc & 0xFF); //les 8 bits de poid faible du crc

        trame[trame.length - 1] = FLAG; //ajoute le flag de fin

        return trame;
    }

    /**
     * Initialise une trame à partir d'un tableau de byte
     * @param trameBytes le tableau de bytes représentant une trame.
     * @return une instance de Trame si le format est valide, sinon null.
     */
    public static Trame donne_to_Trame(byte[] trameBytes) {
        //Si les flags ne sont pas au début et à la fin on retourne null
        if (trameBytes[0] != FLAG || trameBytes[trameBytes.length - 1] != FLAG) return null;

        TrameType type = TrameType.fromCode(trameBytes[1]);
        if (type == null) return null; //Si le type n'existe pas on retourne null

        byte num = trameBytes[2];
        //On destuffe les données sans le crc et le flag
        byte[] donne_destuffed = bitdestuffing(Arrays.copyOfRange(trameBytes, 3, trameBytes.length - 3));


        return new Trame(type, num, donne_destuffed);
    }

    /**
     * Si il y a 5 bits a 1 d'affilés on insere après un bit a 0 du bit sutffing
     * @param donne le tableau de bytes sur lequel appliqué le bit stuffing
     * @return les données avec le bit stuffing d'appliquer
     */
    private static byte[] bitstuffing(byte[] donne){
        List<Byte> donne_stuffed = new ArrayList<>();
        int compteur1 = 0;

        for (byte b : donne) { //Itere bytes
            for (int i = 7; i >= 0; i--) { //Itere bits 
                if ((b & (1 << i)) != 0) { //Si le bit est a 1
                    compteur1++;
                    donne_stuffed.add((byte) 1);
                    if (compteur1 == 5) { //Si 5 bits a 1 d'affilé
                        donne_stuffed.add((byte) 0); //Insere 0
                        compteur1 = 0;
                    }
                } else {
                    compteur1 = 0;
                    donne_stuffed.add((byte) 0);
                }
            }
        }
        return convertBitListToByteArray(donne_stuffed);
    }

    /**
     * Si il y a 5 bits a 1 d'affilés on enleve le 0 du bit stuffing
     * @param donne le tableau de bytes sur lequel enlevé le bit stuffing
     * @return les données sans le bit stuffing
     */
    private static byte[] bitdestuffing(byte[] donne){
        List<Byte> donne_destuffed = new ArrayList<>();
        int compteur1 = 0;

        for (byte b : donne) { //Itere bytes
            for (int i = 7; i >= 0; i--) { //Itere bits 
                if ((b & (1 << i)) != 0) { //Si le bit est a 1
                    compteur1++;
                    donne_destuffed.add((byte) 1);
                    if (compteur1 == 5) { //Si 5 bits a 1 d'affilé
                        i--; //On skip le 0 du bit stuffing
                        compteur1 = 0;
                    }
                } else {
                    compteur1 = 0;
                    donne_destuffed.add((byte) 0);
                }
            }
        }
        return convertBitListToByteArray(donne_destuffed);
    }

    /**
     * Si il y a 5 bits a 1 d'affilés on enleve le 0 du bit stuffing
     * @param bitList une liste de byte ou chaque byte est considéré comme un bit
     * @return la liste convertit en byte Array
     */
    private static byte[] convertBitListToByteArray(List<Byte> bitList) {
        //On arrondit au supérieur pour le nombre de bytes
        byte[] byteArray = new byte[(int) Math.ceil((double) bitList.size() / 8)]; 
        int byteIndex = 0, bitIndex = 7;
    
        for (byte bit : bitList) { //Itere chaque bit (stocker dans byte ici)
            if (bit == 1) {
                byteArray[byteIndex] |= (1 << bitIndex); //On ajuste le bit correspondant
            }
            bitIndex--;
            if (bitIndex < 0) { 
                bitIndex = 7;
                byteIndex++; //On avance au prochain byte
            }
        }
    
        return byteArray;
    }

    /**
     * Accesseur de num
     *
     * @return le numéro de la trame.
     */
    public byte getNum() {
        return num;
    }

    /**
     * Accesseur de crc
     *
     * @return le CRC de la trame.
     */
    public short getCrc() {
        return crc;
    }

    /**
     * Accesseur de type
     *
     * @return le type de la trame.
     */
    public TrameType getType() {
        return type;
    }

    /**
     * Accesseur de donne
     *
     * @return les données de la trame.
     */
    public byte[] getDonne() {
        return donne;
    }

    @Override
    public String toString() {
        return "Type      : " + getType() +
               "\nNuméro    : " + getNum() +
               "\nDonnées   : " + new String(getDonne()) +
               "\nCRC       : " + getCrc() +
               "\n-----------------------------------" + "\n";
    }
}
