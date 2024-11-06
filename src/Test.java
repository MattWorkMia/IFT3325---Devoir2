public class Test {
    public static void main(String[] args) {
        // Sample data for testing
        TrameType type = TrameType.I;  // Use 'I' frame type as an example
        byte num = 1;                  // Frame number
        byte[] donne = {0x12};  // Sample data payload

        // Create Trame object
        Trame trame = new Trame(type, num, donne);

        // Print out the CRC result
        System.out.println("Calculated CRC: " + Integer.toHexString(trame.getCrc()& 0xFFFF));
    }
}
