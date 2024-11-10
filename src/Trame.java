import java.util.Arrays;

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
        byte[] trame = new byte[6 + donne.length];

        trame[0] = FLAG; //ajoute le flag de début
        trame[1] = type.getCode(); 
        trame[2] = num; 

        System.arraycopy(donne, 0, trame, 3, donne.length);

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
        //On copie de 3 (pour enlever le flag, le type et le num) jusqu'à length - 3 (pour enlever le crc et le flag)
        byte[] data = Arrays.copyOfRange(trameBytes, 3, trameBytes.length - 3);

        return new Trame(type, num, data);
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
        return "Type: " + this.type + " " + "Num: " + this.num + "\n";
    }
}
