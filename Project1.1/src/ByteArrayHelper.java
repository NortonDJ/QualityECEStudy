import java.nio.ByteBuffer;

/**
 * This class contains all methods of converting data types:
 * byte array to float, int , String, char
 * float, int , char, string to byte array
 *
 * @author Darren Norton, Yizhong Chen
 * @since Feb-19th-2017
 */
public class ByteArrayHelper {
    public static final byte LF = 12;
    public static final byte CR = 15;
    public static final byte SP = 16;
    public static final byte[] synack = {1, 2, 3};
    public static final byte[] syn = {4, 5, 6};
    public static final byte[] ack = {7, 8, 9};

    /**
     * convert float to byte array
     *
     * @param float
     * @return byte array
     */
    public static byte[] toByteArray(float value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        return buffer.array();
    }

    /**
     * convert int to byte array
     *
     * @param int
     * @return byte array
     */
    public static byte[] toByteArray(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        return buffer.array();
    }

    /**
     * convert String to byte array
     *
     * @param String
     * @return byte array
     */
    public static byte[] toByteArray(String value) {
        return value.getBytes();
    }

    /**
     * convert byte array to int
     *
     * @param byte array
     * @return int
     */
    public static int toInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * convert byte array to char
     *
     * @param byte array
     * @return char
     */
    public static char toChar(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getChar();
    }

    /**
     * convert byte array to String
     *
     * @param byte array
     * @return String
     */
    public static String tostring(byte[] bytes) {
        String s = new String(bytes);
        return s;
    }

    /**
     * convert byte array to float
     *
     * @param byte array
     * @return float
     */
    public static float toFloat(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getFloat();
    }
}
