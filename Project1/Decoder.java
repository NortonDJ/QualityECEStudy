
/**
 * Write a description of interface Decoder here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public interface Decoder
{
    /**
     * Decode the received message so that HTTP can read
     * 
     * @param a byte array which received 
     * @return decoded byte array
     */
    byte[] decode(byte[] message);
}
