enum TrameType {
    I((byte) 'I'),   // Information frame
    C((byte) 'C'),   // Connection request
    A((byte) 'A'),   // Acknowledgment
    R((byte) 'R'),   // Rejection
    F((byte) 'F'),   // End of communication
    P((byte) 'P');   // Frame with P bit

    private final byte code;

    TrameType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}


class Trame{
    private static final short CRC_CCIT = 0x1021;
    private static final byte flag = (byte) 126;
    private byte num;
    private byte[] donne;
    private short crc;
    private TrameType type;

    Trame(TrameType type, byte num, byte[] donne){
        this.type = type;
        this.num = num;
        this.donne = donne;
        this.crc = calculateCrc();
    }

    private short calculateCrc(){
        byte[] donne_checksum = new byte[donne.length + 2];
        donne_checksum[0] = type.getCode();
        donne_checksum[1] = num;
        System.arraycopy(donne, 0, donne_checksum, 2, donne.length);
        int registre = 0x0000;

        for (byte b : donne_checksum) {
            registre ^= (b << 8);

            for (int i = 0; i < 8; i++) {
                if ((registre & 0x8000) != 0) 
                    registre = (registre << 1) ^ CRC_CCIT;
                else 
                    registre <<= 1;
            }
        }
        return (short) (registre & 0xFFFF);
    }

    public byte getNum(){
        return num;
    }

    public short getCrc(){
        return crc;
    }

    public byte getType(){
        return type.getCode();
    }

    
}
