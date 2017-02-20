import java.nio.ByteBuffer;
/**
 * Created by nortondj on 2/19/17.
 */
public class ByteArrayHelper {
    public static final byte LF = 12;
    public static final byte CR = 15;
    public static final byte SP = 16;
    
    public static void main(String[] arg){
        String string1 = "yizhong Chen";
        ByteArrayHelper bah = new ByteArrayHelper();
        System.out.println(string1);
    }
    
    public static byte[] toByteArray(float value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        return buffer.array();
    }

    public static byte[] toByteArray(int value){
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        return buffer.array();
    }

    public static byte[] toByteArray(String value){
        return value.getBytes();
    }
    
    public static int toInt(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static char toChar(byte[] bytes){
        return ByteBuffer.wrap(bytes).getChar();
    }
    
    public static String tostring(byte[] bytes){
        String s = new String(bytes);
        return s;
    }
    
    public static float toFloat(byte[] bytes){
        return ByteBuffer.wrap(bytes).getFloat();
    }
}
