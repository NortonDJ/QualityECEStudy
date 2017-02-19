import java.nio.ByteBuffer;

/**
 * Created by nortondj on 2/19/17.
 */
public class ByteArrayHelper {

    public static byte[] toByteArray(float value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putFloat(value);
        return buffer.array();
    }

    public static float toFloat(byte[] bytes){
        return ByteBuffer.wrap(bytes).getFloat();
    }
}
